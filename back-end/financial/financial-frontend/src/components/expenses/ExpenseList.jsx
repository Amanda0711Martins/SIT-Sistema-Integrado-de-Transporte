// src/components/expenses/ExpenseList.jsx
import React, { useState, useEffect } from 'react';
import { Table, Button, Spinner, Alert, Row, Col, Form, InputGroup, Badge } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEdit, faTrash, faSearch, faFilter, faPlus } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';
import { getExpenses, deleteExpense } from '../../services/expenseService';
import { formatCurrency, formatDate } from '../../utils/formatters';

const ExpenseList = () => {
    const [expenses, setExpenses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [searchTerm, setSearchTerm] = useState('');
    const [expenseType, setExpenseType] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(null);

    const navigate = useNavigate();

    useEffect(() => {
        fetchExpenses();
    }, [page, size, expenseType]);

    const fetchExpenses = async () => {
        try {
            setLoading(true);
            let params = { page, size };
            if (expenseType) {
                const response = await getExpenses(`/type/${expenseType}`, params);
                setExpenses(response.content);
                setTotalPages(response.totalPages);
            } else {
                const response = await getExpenses('', params);
                setExpenses(response.content);
                setTotalPages(response.totalPages);
            }
        } catch (err) {
            setError(`Failed to load expenses: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        // Search implementation would go here
        // For simplicity, we're just logging the search term
        console.log(`Searching for: ${searchTerm}`);
    };

    const handleTypeFilterChange = (e) => {
        setExpenseType(e.target.value);
        setPage(0);
    };

    const handleConfirmDelete = async (id) => {
        try {
            await deleteExpense(id);
            setConfirmDelete(null);
            fetchExpenses();
        } catch (err) {
            setError(`Failed to delete expense: ${err.message}`);
        }
    };

    const handleAddNew = () => {
        navigate('/finances/expenses/new');
    };

    const handleEdit = (id) => {
        navigate(`/finances/expenses/${id}`);
    };

    const renderExpenseTypeLabel = (type) => {
        const typeColorMap = {
            FUEL: 'primary',
            MAINTENANCE: 'warning',
            TOLL: 'info',
            SALARY: 'danger',
            RENT: 'secondary',
            UTILITIES: 'dark',
            SUPPLIES: 'success',
            TAX: 'danger',
            OTHER: 'light'
        };

        return <Badge bg={typeColorMap[type] || 'secondary'}>{type}</Badge>;
    };

    if (loading && expenses.length === 0) {
        return <div className="text-center p-5"><Spinner animation="border" /></div>;
    }

    return (
        <div className="expense-list-container">
            <h2 className="mb-4">Operational Expenses</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            <Row className="mb-4">
                <Col md={6}>
                    <Form onSubmit={handleSearch}>
                        <InputGroup>
                            <Form.Control
                                type="text"
                                placeholder="Search expenses..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                            <Button variant="outline-secondary" type="submit">
                                <FontAwesomeIcon icon={faSearch} />
                            </Button>
                        </InputGroup>
                    </Form>
                </Col>
                <Col md={4}>
                    <Form.Select
                        value={expenseType}
                        onChange={handleTypeFilterChange}
                    >
                        <option value="">All Expense Types</option>
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
                </Col>
                <Col md={2} className="text-end">
                    <Button variant="success" onClick={handleAddNew}>
                        <FontAwesomeIcon icon={faPlus} /> Add New
                    </Button>
                </Col>
            </Row>

            <Table striped bordered hover responsive>
                <thead>
                <tr>
                    <th>Description</th>
                    <th>Amount</th>
                    <th>Type</th>
                    <th>Category</th>
                    <th>Date</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {expenses.length === 0 ? (
                    <tr>
                        <td colSpan="6" className="text-center">No expenses found</td>
                    </tr>
                ) : (
                    expenses.map(expense => (
                        <tr key={expense.id}>
                            <td>{expense.description}</td>
                            <td>{formatCurrency(expense.amount)}</td>
                            <td>{renderExpenseTypeLabel(expense.type)}</td>
                            <td>{expense.category}</td>
                            <td>{formatDate(expense.expenseDate)}</td>
                            <td>
                                <Button
                                    variant="outline-primary"
                                    size="sm"
                                    className="me-2"
                                    onClick={() => handleEdit(expense.id)}
                                >
                                    <FontAwesomeIcon icon={faEdit} />
                                </Button>
                                <Button
                                    variant="outline-danger"
                                    size="sm"
                                    onClick={() => setConfirmDelete(expense.id)}
                                >
                                    <FontAwesomeIcon icon={faTrash} />
                                </Button>
                            </td>
                        </tr>
                    ))
                )}
                </tbody>
            </Table>

            <div className="d-flex justify-content-between align-items-center">
                <div>
                    Showing {expenses.length} of {totalPages * size} entries
                </div>
                <div>
                    <Button
                        variant="outline-primary"
                        className="me-2"
                        disabled={page === 0}
                        onClick={() => setPage(page - 1)}
                    >
                        Previous
                    </Button>
                    <Button
                        variant="outline-primary"
                        disabled={page >= totalPages - 1}
                        onClick={() => setPage(page + 1)}
                    >
                        Next
                    </Button>
                </div>
            </div>

            {/* Delete Confirmation Modal */}
            {confirmDelete && (
                <div className="modal show d-block" tabIndex="-1">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Confirm Delete</h5>
                                <button type="button" className="btn-close" onClick={() => setConfirmDelete(null)}></button>
                            </div>
                            <div className="modal-body">
                                Are you sure you want to delete this expense? This action cannot be undone.
                            </div>
                            <div className="modal-footer">
                                <Button variant="secondary" onClick={() => setConfirmDelete(null)}>
                                    Cancel
                                </Button>
                                <Button variant="danger" onClick={() => handleConfirmDelete(confirmDelete)}>
                                    Delete
                                </Button>
                            </div>
                        </div>
                    </div>
                    <div className="modal-backdrop show"></div>
                </div>
            )}
        </div>
    );
};

export default ExpenseList;