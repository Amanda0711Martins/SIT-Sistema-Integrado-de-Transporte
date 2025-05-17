// tests/components/employee/EmployeeForm.test.jsx
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { toast } from 'react-toastify';
import EmployeeForm from '../../../src/components/employee/EmployeeForm';
import { employeeService } from '../../../src/services/employeeService';

// Mock the services and dependencies
jest.mock('../../../src/services/employeeService');
jest.mock('react-toastify');

// Mock react-router-dom hooks
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useParams: () => ({ id: 'new' }),
    useNavigate: () => jest.fn()
}));

describe('EmployeeForm Component - Create Mode', () => {
    beforeEach(() => {
        // Reset mock functions before each test
        jest.clearAllMocks();
    });

    test('renders form with empty fields for new employee', () => {
        render(
            <BrowserRouter>
                <EmployeeForm />
            </BrowserRouter>
        );

        // Verify the form title
        expect(screen.getByText('Add New Employee')).toBeInTheDocument();

        // Verify input fields exist and are empty
        expect(screen.getByLabelText('Full Name')).toHaveValue('');
        expect(screen.getByLabelText('Email')).toHaveValue('');
        expect(screen.getByLabelText('CPF')).toHaveValue('');
        expect(screen.getByLabelText('Position')).toHaveValue('');

        // Verify buttons exist
        expect(screen.getByText('Cancel')).toBeInTheDocument();
        expect(screen.getByText('Create Employee')).toBeInTheDocument();
    });

    test('validates required fields', async () => {
        render(
            <BrowserRouter>
                <EmployeeForm />
            </BrowserRouter>
        );

        // Click submit without filling in required fields
        fireEvent.click(screen.getByText('Create Employee'));

        // Wait for validation errors to appear
        await waitFor(() => {
            expect(screen.getByText('Name is required')).toBeInTheDocument();
            expect(screen.getByText('Email is required')).toBeInTheDocument();
            expect(screen.getByText('Position is required')).toBeInTheDocument();
            expect(screen.getByText('Department is required')).toBeInTheDocument();
            expect(screen.getByText('CPF is required')).toBeInTheDocument();
        });
    });

    test('submits form with valid data', async () => {
        employeeService.createEmployee.mockResolvedValue({
            id: 1,
            name: 'John Doe',
            email: 'john.doe@example.com'
        });

        render(
            <BrowserRouter>
                <EmployeeForm />
            </BrowserRouter>
        );

        // Fill in form fields
        fireEvent.change(screen.getByLabelText('Full Name'), {
            target: { value: 'John Doe' }
        });

        fireEvent.change(screen.getByLabelText('Email'), {
            target: { value: 'john.doe@example.com' }
        });

        fireEvent.change(screen.getByLabelText('CPF'), {
            target: { value: '123.456.789-00' }
        });

        fireEvent.change(screen.getByLabelText('Position'), {
            target: { value: 'Developer' }
        });

        fireEvent.change(screen.getByLabelText('Base Salary'), {
            target: { value: '5000' }
        });

        // Select department
        fireEvent.change(screen.getByLabelText('Department'), {
            target: { value: 'IT' }
        });

        // Select dates
        fireEvent.change(screen.getByLabelText('Hire Date'), {
            target: { value: '2022-01-15' }
        });

        fireEvent.change(screen.getByLabelText('Birth Date'), {
            target: { value: '1990-05-20' }
        });

        // Submit the form
        fireEvent.click(screen.getByText('Create Employee'));

        // Wait for service call
        await waitFor(() => {
            expect(employeeService.createEmployee).toHaveBeenCalledWith({
                name: 'John Doe',
                email: 'john.doe@example.com',
                cpf: '123.456.789-00',
                position: 'Developer',
                department: 'IT',
                baseSalary: '5000',
                hireDate: '2022-01-15',
                birthDate: '1990-05-20',
                active: true
            });

            expect(toast.success).toHaveBeenCalledWith('Employee created successfully');
        });
    });

    test('handles API error during submission', async () => {
        employeeService.createEmployee.mockRejectedValue(new Error('API Error'));

        render(
            <BrowserRouter>
                <EmployeeForm />
            </BrowserRouter>
        );

        // Fill in form fields (minimum required)
        fireEvent.change(screen.getByLabelText('Full Name'), {
            target: { value: 'John Doe' }
        });

        fireEvent.change(screen.getByLabelText('Email'), {
            target: { value: 'john.doe@example.com' }
        });

        fireEvent.change(screen.getByLabelText('CPF'), {
            target: { value: '123.456.789-00' }
        });

        fireEvent.change(screen.getByLabelText('Position'), {
            target: { value: 'Developer' }
        });

        fireEvent.change(screen.getByLabelText('Department'), {
            target: { value: 'IT' }
        });

        fireEvent.change(screen.getByLabelText('Base Salary'), {
            target: { value: '5000' }
        });

        fireEvent.change(screen.getByLabelText('Hire Date'), {
            target: { value: '2022-01-15' }
        });

        fireEvent.change(screen.getByLabelText('Birth Date'), {
            target: { value: '1990-05-20' }
        });

        // Submit the form
        fireEvent.click(screen.getByText('Create Employee'));

        // Verify error handling
        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Failed to create employee');
        });
    });
});