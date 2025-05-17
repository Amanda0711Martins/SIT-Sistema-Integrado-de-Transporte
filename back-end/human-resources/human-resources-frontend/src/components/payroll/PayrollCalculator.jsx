// src/components/payroll/PayrollCalculator.jsx
import React, { useState, useEffect } from 'react';
import { Card, Form, Button, Row, Col, Alert } from 'react-bootstrap';
import { employeeService } from '../../services/employeeService';
import { payrollService } from '../../services/payrollService';
import Loader from '../common/Loader';
import { toast } from 'react-toastify';
import { formatCurrency } from '../../utils/formatter';

const PayrollCalculator = () => {
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [calculating, setCalculating] = useState(false);
    const [calculatedPayroll, setCalculatedPayroll] = useState(null);
    const [formData, setFormData] = useState({
        employeeId: '',
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1,
        bonuses: 0,
        otherDeductions: 0
    });

    useEffect(() => {
        loadEmployees();
    }, []);

    const loadEmployees = async () => {
        setLoading(true);
        try {
            const data = await employeeService.getActiveEmployees();
            setEmployees(data);
        } catch (error) {
            toast.error('Failed to load employees');
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setCalculating(true);
        setCalculatedPayroll(null);

        try {
            const { employeeId, year, month, bonuses, otherDeductions } = formData;

            if (!employeeId) {
                toast.error('Please select an employee');
                return;
            }

            const result = await payrollService.calculatePayroll(
                employeeId,
                parseInt(year),
                parseInt(month),
                parseFloat(bonuses) || 0,
                parseFloat(otherDeductions) || 0
            );

            setCalculatedPayroll(result);
            toast.success('Payroll calculated successfully');
        } catch (error) {
            toast.error('Failed to calculate payroll');
        } finally {
            setCalculating(false);
        }
    };

    if (loading) return <Loader />;

    return (
        <Card className="shadow">
            <Card.Header className="bg-primary text-white">
                <h5 className="mb-0">Calculate Payroll</h5>
            </Card.Header>
            <Card.Body>
                <Form onSubmit={handleSubmit}>
                    <Row>
                        <Col md={6}>
                            <Form.Group className="mb-3">
                                <Form.Label>Employee</Form.Label>
                                <Form.Select
                                    name="employeeId"
                                    value={formData.employeeId}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="">Select Employee</option>
                                    {employees.map((employee) => (
                                        <option key={employee.id} value={employee.id}>
                                            {employee.name} - {employee.position}
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>

                        <Col md={3}>
                            <Form.Group className="mb-3">
                                <Form.Label>Year</Form.Label>
                                <Form.Control
                                    type="number"
                                    name="year"
                                    value={formData.year}
                                    onChange={handleChange}
                                    min={2000}
                                    max={2100}
                                    required
                                />
                            </Form.Group>
                        </Col>

                        <Col md={3}>
                            <Form.Group className="mb-3">
                                <Form.Label>Month</Form.Label>
                                <Form.Select
                                    name="month"
                                    value={formData.month}
                                    onChange={handleChange}
                                    required
                                >
                                    {Array.from({ length: 12 }, (_, i) => (
                                        <option key={i + 1} value={i + 1}>
                                            {new Date(0, i).toLocaleString('default', { month: 'long' })}
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                    </Row>

                    <Row>
                        <Col md={6}>
                            <Form.Group className="mb-3">
                                <Form.Label>Bonuses</Form.Label>
                                <Form.Control
                                    type="number"
                                    name="bonuses"
                                    value={formData.bonuses}
                                    onChange={handleChange}
                                    step="0.01"
                                    min="0"
                                />
                            </Form.Group>
                        </Col>

                        <Col md={6}>
                            <Form.Group className="mb-3">
                                <Form.Label>Other Deductions</Form.Label>
                                <Form.Control
                                    type="number"
                                    name="otherDeductions"
                                    value={formData.otherDeductions}
                                    onChange={handleChange}
                                    step="0.01"
                                    min="0"
                                />
                            </Form.Group>
                        </Col>
                    </Row>

                    <div className="d-flex justify-content-end mt-4">
                        <Button
                            type="submit"
                            variant="primary"
                            disabled={calculating}
                        >
                            {calculating ? 'Calculating...' : 'Calculate Payroll'}
                        </Button>
                    </div>
                </Form>

                {calculatedPayroll && (
                    <div className="mt-4">
                        <h5>Calculation Result</h5>
                        <Card>
                            <Card.Body>
                                <Row>
                                    <Col md={6}>
                                        <p><strong>Employee:</strong> {calculatedPayroll.employeeName}</p>
                                        <p><strong>Period:</strong> {new Date(0, calculatedPayroll.yearMonth.monthValue - 1).toLocaleString('default', { month: 'long' })} {calculatedPayroll.yearMonth.year}</p>
                                        <p><strong>Status:</strong> {calculatedPayroll.status}</p>
                                        <p><strong>Processed Date:</strong> {new Date(calculatedPayroll.processedAt).toLocaleDateString()}</p>
                                    </Col>
                                    <Col md={6}>
                                        <p><strong>Gross Salary:</strong> {formatCurrency(calculatedPayroll.grossSalary)}</p>
                                        <p><strong>INSS Deduction:</strong> {formatCurrency(calculatedPayroll.inssDeduction)}</p>
                                        <p><strong>Income Tax Deduction:</strong> {formatCurrency(calculatedPayroll.incomeTaxDeduction)}</p>
                                        <p><strong>Other Deductions:</strong> {formatCurrency(calculatedPayroll.otherDeductions)}</p>
                                        <p><strong>Bonuses:</strong> {formatCurrency(calculatedPayroll.bonuses)}</p>
                                        <p className="text-primary fw-bold">
                                            <strong>Net Salary:</strong> {formatCurrency(calculatedPayroll.netSalary)}
                                        </p>
                                    </Col>
                                </Row>

                                <div className="d-flex justify-content-end mt-3">
                                    <Button
                                        variant="success"
                                        onClick={async () => {
                                            try {
                                                await payrollService.updatePayrollStatus(calculatedPayroll.id, 'PAID');
                                                toast.success('Payroll marked as paid');

                                                // Refresh calculation
                                                const result = await payrollService.getPayrollById(calculatedPayroll.id);
                                                setCalculatedPayroll(result);
                                            } catch (error) {
                                                toast.error('Failed to update payroll status');
                                            }
                                        }}
                                        disabled={calculatedPayroll.status === 'PAID'}
                                    >
                                        {calculatedPayroll.status === 'PAID' ? 'Payment Processed' : 'Process Payment'}
                                    </Button>
                                </div>

                                {calculatedPayroll.status === 'PAID' && (
                                    <Alert variant="success" className="mt-3">
                                        This payroll was paid on {new Date(calculatedPayroll.paymentDate).toLocaleDateString()}.
                                    </Alert>
                                )}
                            </Card.Body>
                        </Card>
                    </div>
                )}
            </Card.Body>
        </Card>
    );
};

export default PayrollCalculator;