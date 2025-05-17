// src/components/expenses/ExpenseForm.jsx
import React, { useState, useEffect } from 'react';
import { Form, Button, Row, Col, Card, Alert, Spinner } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { getExpenseById, createExpense, updateExpense } from '../../services/expenseService';
import { formatISODate } from '../../utils/formatters';

const ExpenseForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEditMode = !!id;

    const [formData, setFormData] = useState({
        description: '',
        amount: '',
        type: '',
        category: '',
        expenseDate: formatISODate(new Date())
    });

    const [loading, setLoading] = useState(isEditMode);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);
    const [validationErrors, setValidationErrors] = useState({});

    useEffect(() => {
        if (isEditMode) {
            fetchExpense();
        }
    }, [id]);

    const fetchExpense = async () => {
        try {
            setLoading(true);
            const data = await getExpenseById(id);
            setFormData({
                description: data.description,
                amount: data.amount.toString(),
                type: data.type,
                category: data.category || '',
                expenseDate: formatISODate(new Date(data.expenseDate))
            });
        } catch (err) {
            setError(`Failed to load expense: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });

        // Clear validation error when field is updated
        if (validationErrors[name]) {
            setValidationErrors({
                ...validationErrors,
                [name]: null
            });
        }
    };

    const validateForm = () => {
        const errors = {};

        if (!formData.description.trim()) {
            errors.description = 'Description is required';
        }

        if (!formData.amount) {
            errors.amount = 'Amount is required';
        } else if (isNaN(parseFloat(formData.amount)) || parseFloat(formData.amount) <= 0) {
            errors.amount = 'Amount must be a positive number';
        }

        if (!formData.type) {
            errors.type = 'Expense type is required';
        }

        if (!formData.expenseDate) {
            errors.expenseDate = 'Expense date is required';
        }

        setValidationErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        try {
            setSaving(true);

            const expenseData = {
                ...formData,
                amount: parseFloat(formData.amount)
            };

            if (isEditMode) {
                await updateExpense(id, expenseData);
            } else {
                await createExpense(expenseData);
            }

            navigate('/finances/expenses');
        } catch (err) {
            setError(`Failed to save expense: ${err.message}`);
            setSaving(false);
        }
    };

    if (loading) {
        return <div className="text-center p-5"><Spinner animation="border" /></div>;
    }

    return (
        <div className="expense-form-container">
            <Card>
                <Card.Header as="h5">{isEditMode ? 'Edit Expense' : 'Add New Expense'}</Card.Header>
                <Card.Body>
                    {error && <Alert variant="danger">{error}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        <Row className="mb-3">
                            <Col md={6}>
                                <Form.Group controlId="description">
                                    <Form.Label>Description *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="description"
                                        value={formData.description}
                                        onChange={handleChange}
                                        isInvalid={!!validationErrors.description}
                                        placeholder="Enter expense description"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {validationErrors.description}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>

                            <Col md={6}>
                                <Form.Group controlId="amount">
                                    <Form.Label>Amount ($) *</Form.Label>
                                    <Form.Control
                                        type="number"
                                        step="0.01"
                                        min="0.01"
                                        name="amount"
                                        value={formData.amount}
                                        onChange={handleChange}
                                        isInvalid={!!validationErrors.amount}
                                        placeholder="Enter amount"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {validationErrors.amount}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row className="mb-3">
                            <Col md={6}>
                                <Form.Group controlId="type">
                                    <Form.Label>Expense Type *</Form.Label>
                                    <Form.Select
                                        name="type"
                                        value={formData.type}
                                        onChange={handleChange}
                                        isInvalid={!!validationErrors.type}
                                    >
                                        <option value="">Select expense type</option>
                                        <option value="FUEL">Fuel</option>
                                        <option value="MAINTENANCE">Maintenance</option>
                                        <option value="TOLL">Toll</option>
                                        <option value="SALARY">Salary</option>
                                        <option value="RENT">Rent</option>
                                        <option value="UTILITIES">Utilities</option>
                                        <option value="SUPPLIES">Supplies</option>
                                        <option value="TAX">Tax</option>
                                        <option value="OTHER">Other</option>
                                    </Form.Select>
                                    <Form.Control.Feedback type="invalid">
                                        {validationErrors.type}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>

                            <Col md={6}>
                                <Form.Group controlId="category">
                                    <Form.Label>Category</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="category"
                                        value={formData.category}
                                        onChange={handleChange}
                                        placeholder="Enter category (optional)"
                                    />
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row className="mb-3">
                            <Col md={6}>
                                <Form.Group controlId="expenseDate">
                                    <Form.Label>Expense Date *</Form.Label>
                                    <Form.Control
                                        type="date"
                                        name="expenseDate"
                                        value={formData.expenseDate}
                                        onChange={handleChange}
                                        isInvalid={!!validationErrors.expenseDate}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {validationErrors.expenseDate}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                        </Row>

                        <div className="d-flex justify-content-end gap-2 mt-4">
                            <Button
                                variant="secondary"
                                onClick={() => navigate('/finances/expenses')}
                                disabled={saving}
                            >
                                Cancel
                            </Button>
                            <Button
                                variant="primary"
                                type="submit"
                                disabled={saving}
                            >
                                {saving ? (
                                    <>
                                        <Spinner
                                            as="span"
                                            animation="border"
                                            size="sm"
                                            role="status"
                                            aria-hidden="true"
                                            className="me-2"
                                        />
                                        Saving...
                                    </>
                                ) : (
                                    isEditMode ? 'Update Expense' : 'Create Expense'
                                )}
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </div>
    );
};

export default ExpenseForm;