// tests/components/payroll/PayrollCalculator.test.jsx
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { toast } from 'react-toastify';
import PayrollCalculator from '../../../src/components/payroll/PayrollCalculator';
import { employeeService } from '../../../src/services/employeeService';
import { payrollService } from '../../../src/services/payrollService';

// Mock the services and dependencies
jest.mock('../../../src/services/employeeService');
jest.mock('../../../src/services/payrollService');
jest.mock('react-toastify');

describe('PayrollCalculator Component', () => {
    beforeEach(() => {
        // Reset mock functions before each test
        jest.clearAllMocks();

        // Mock employee data
        employeeService.getActiveEmployees.mockResolvedValue([
            { id: 1, name: 'John Doe', position: 'Developer' },
            { id: 2, name: 'Jane Smith', position: 'Manager' }
        ]);
    });

    test('renders form with employee dropdown', async () => {
        render(<PayrollCalculator />);

        // Wait for employee data to load
        await waitFor(() => {
            expect(screen.getByText('Calculate Payroll')).toBeInTheDocument();
            expect(screen.getByText('Select Employee')).toBeInTheDocument();
            expect(screen.getByText('John Doe - Developer')).toBeInTheDocument();
            expect(screen.getByText('Jane Smith - Manager')).toBeInTheDocument();
        });
    });

    test('shows error when no employee is selected', async () => {
        render(<PayrollCalculator />);

        // Wait for employee data to load
        await waitFor(() => {
            expect(screen.getByText('Calculate Payroll')).toBeInTheDocument();
        });

        // Try to submit without selecting employee
        fireEvent.click(screen.getByText('Calculate Payroll'));

        expect(toast.error).toHaveBeenCalledWith('Please select an employee');
    });

    test('calculates payroll successfully', async () => {
        // Mock payroll calculation result
        payrollService.calculatePayroll.mockResolvedValue({
            id: 1,
            employeeId: 1,
            employeeName: 'John Doe',
            yearMonth: {
                year: 2023,
                monthValue: 5
            },
            grossSalary: 5000,
            inssDeduction: 550,
            incomeTaxDeduction: 500,
            otherDeductions: 200,
            bonuses: 500,
            netSalary: 4250,
            processedAt: '2023-05-15',
            status: 'PROCESSED'
        });

        render(<PayrollCalculator />);

        // Wait for employee data to load
        await waitFor(() => {
            expect(screen.getByText('John Doe - Developer')).toBeInTheDocument();
        });

        // Select employee
        fireEvent.change(screen.getByLabelText('Employee'), {
            target: { value: '1' }
        });

        // Add bonuses and deductions
        fireEvent.change(screen.getByLabelText('Bonuses'), {
            target: { value: '500' }
        });

        fireEvent.change(screen.getByLabelText('Other Deductions'), {
            target: { value: '200' }
        });

        // Submit the form
        fireEvent.click(screen.getByText('Calculate Payroll'));

        // Wait for calculation to complete
        await waitFor(() => {
            expect(payrollService.calculatePayroll).toHaveBeenCalledWith(
                '1', // employeeId
                expect.any(Number), // current year
                expect.any(Number), // current month
                500, // bonuses
                200  // otherDeductions
            );

            expect(toast.success).toHaveBeenCalledWith('Payroll calculated successfully');

            // Check result display
            expect(screen.getByText('Calculation Result')).toBeInTheDocument();
            expect(screen.getByText('John Doe')).toBeInTheDocument();
            expect(screen.getByText('Process Payment')).toBeInTheDocument();
        });
    });

    test('handles API error during calculation', async () => {
        // Mock API error
        payrollService.calculatePayroll.mockRejectedValue(new Error('API Error'));

        render(<PayrollCalculator />);

        // Wait for employee data to load
        await waitFor(() => {
            expect(screen.getByText('John Doe - Developer')).toBeInTheDocument();
        });

        // Select employee
        fireEvent.change(screen.getByLabelText('Employee'), {
            target: { value: '1' }
        });

        // Submit the form
        fireEvent.click(screen.getByText('Calculate Payroll'));

        // Verify error handling
        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Failed to calculate payroll');
        });
    });

    test('updates payroll status to PAID', async () => {
        // Mock payroll calculation and status update
        payrollService.calculatePayroll.mockResolvedValue({
            id: 1,
            employeeId: 1,
            employeeName: 'John Doe',
            yearMonth: {
                year: 2023,
                monthValue: 5
            },
            grossSalary: 5000,
            inssDeduction: 550,
            incomeTaxDeduction: 500,
            otherDeductions: 0,
            bonuses: 0,
            netSalary: 3950,
            processedAt: '2023-05-15',
            status: 'PROCESSED'
        });

        payrollService.updatePayrollStatus.mockResolvedValue({
            id: 1,
            employeeId: 1,
            employeeName: 'John Doe',
            status: 'PAID',
            paymentDate: '2023-05-15'
        });

        payrollService.getPayrollById.mockResolvedValue({
            id: 1,
            employeeId: 1,
            employeeName: 'John Doe',
            yearMonth: {
                year: 2023,
                monthValue: 5
            },
            grossSalary: 5000,
            inssDeduction: 550,
            incomeTaxDeduction: 500,
            otherDeductions: 0,
            bonuses: 0,
            netSalary: 3950,
            processedAt: '2023-05-15',
            status: 'PAID',
            paymentDate: '2023-05-15'
        });

        render(<PayrollCalculator />);

        // Wait for employee data to load and select employee
        await waitFor(() => {
            expect(screen.getByText('John Doe - Developer')).toBeInTheDocument();
        });

        fireEvent.change(screen.getByLabelText('Employee'), {
            target: { value: '1' }
        });

        // Submit the form to calculate payroll
        fireEvent.click(screen.getByText('Calculate Payroll'));

        // Wait for calculation result
        await waitFor(() => {
            expect(screen.getByText('Calculation Result')).toBeInTheDocument();
            expect(screen.getByText('Process Payment')).toBeInTheDocument();
        });

        // Click process payment button
        fireEvent.click(screen.getByText('Process Payment'));

        // Verify status update
        await waitFor(() => {
            expect(payrollService.updatePayrollStatus).toHaveBeenCalledWith(1, 'PAID');
            expect(toast.success).toHaveBeenCalledWith('Payroll marked as paid');
            expect(screen.getByText('Payment Processed')).toBeInTheDocument();
        });
    });
});