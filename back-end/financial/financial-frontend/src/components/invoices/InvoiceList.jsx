// src/components/invoices/InvoiceList.jsx
import React, { useState, useEffect } from 'react';
import { Table, Button, Spinner, Alert, Row, Col, Form, InputGroup, Badge, Dropdown } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEdit, faTrash, faSearch, faFilter, faPlus, faFileInvoice, faBan } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';
import { getInvoices, deleteInvoice, issueInvoice, cancelInvoice } from '../../services/invoiceService';
import { formatCurrency, formatDate } from '../../utils/formatters';

const InvoiceList = () => {
    const [invoices, setInvoices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [searchTerm, setSearchTerm] = useState('');
    const [invoiceStatus, setInvoiceStatus] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(null);
    const [actionLoading, setActionLoading] = useState(null);

    const navigate = useNavigate();

    useEffect(() => {
        fetchInvoices();
    }, [page, size, invoiceStatus]);

    const fetchInvoices = async () => {
        try {
            setLoading(true);
            let params = { page, size };
            let endpoint = '';

            if (invoiceStatus) {
                endpoint = `/status/${invoiceStatus}`;
            }

            const response = await getInvoices(endpoint, params);
            setInvoices(response.content);
            setTotalPages(response.totalPages);
        } catch (err) {
            setError(`Failed to load invoices: ${err.message}`);
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

    const handleStatusFilterChange = (e) => {
        setInvoiceStatus(e.target.value);
        setPage(0);
    };

    const handleConfirmDelete = async (id) => {
        try {
            await deleteInvoice(id);
            setConfirmDelete(null);
            fetchInvoices();
        } catch (err) {
            setError(`Failed to delete invoice: ${err.message}`);
        }
    };

    const handleAddNew = () => {
        navigate('/finances/invoices/new');
    };

    const handleEdit = (id) => {
        navigate(`/finances/invoices/${id}`);
    };

    const handleIssueInvoice = async (id) => {
        try {
            setActionLoading(id);
            await issueInvoice(id);
            fetchInvoices();
        } catch (err) {
            setError(`Failed to issue invoice: ${err.message}`);
        } finally {
            setActionLoading(null);
        }
    };

    const handleCancelInvoice = async (id) => {
        try {
            setActionLoading(id);
            await cancelInvoice(id);
            fetchInvoices();
        } catch (err) {
            setError(`Failed to cancel invoice: ${err.message}`);
        } finally {
            setActionLoading(null);
        }
    };

    const renderStatusBadge = (status) => {
        const statusColorMap = {
            DRAFT: 'secondary',
            ISSUED: 'success',
            PAID: 'primary',
            CANCELLED: 'danger',
            OVERDUE: 'warning'
        };

        return <Badge bg={statusColorMap[status] || 'info'}>{status}</Badge>;
    };

    if (loading && invoices.length === 0) {
        return <div className="text-center p-5"><Spinner animation="border" /></div>;
    }

    return (
        <div className="invoice-list-container">
            <h2 className="mb-4">Invoices</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            <Row className="mb-4">
                <Col md={5}>
                    <Form onSubmit={handleSearch}>
                        <InputGroup>
                            <Form.Control
                                type="text"
                                placeholder="Search invoices..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                            <Button variant="outline-secondary" type="submit">
                                <FontAwesomeIcon icon={faSearch} />
                            </Button>
                        </InputGroup>
                    </Form>
                </Col>
                <Col md={5}>
                    <Form.Select
                        value={invoiceStatus}
                        onChange={handleStatusFilterChange}
                    >
                        <option value="">All Statuses</option>
                        <option value="DRAFT">Draft</option>
                        <option value="ISSUED">Issued</option>
                        <option value="PAID">Paid</option>
                        <option value="CANCELLED">Cancelled</option>
                        <option value="OVERDUE">Overdue</option>
                    </Form.Select>
                </Col>
                <Col md={2} className="text-end">
                    <Button variant="success" onClick={handleAddNew}>
                        <FontAwesomeIcon icon={faPlus} /> New Invoice
                    </Button>
                </Col>
            </Row>

            <Table striped bordered hover responsive>
                <thead>
                <tr>
                    <th>Invoice #</th>
                    <th>Client</th>
                    <th>Amount</th>
                    <th>Issue Date</th>
                    <th>Due Date</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {invoices.length === 0 ? (
                    <tr>
                        <td colSpan="7" className="text-center">No invoices found</td>
                    </tr>
                ) : (
                    invoices.map(invoice => (
                        <tr key={invoice.id}>
                            <td>{invoice.invoiceNumber}</td>
                            <td>{invoice.clientName}</td>
                            <td>{formatCurrency(invoice.amount)}</td>
                            <td>{formatDate(invoice.issueDate)}</td>
                            <td>{invoice.dueDate ? formatDate(invoice.dueDate) : "-"}</td>
                            <td>{renderStatusBadge(invoice.status)}</td>
                            <td>
                                <Dropdown>
                                    <Dropdown.Toggle variant="outline-secondary" size="sm" id={`dropdown-invoice-${invoice.id}`}>
                                        Actions
                                    </Dropdown.Toggle>
                                    <Dropdown.Menu>
                                        {invoice.status === 'DRAFT' && (
                                            <>
                                                <Dropdown.Item onClick={() => handleEdit(invoice.id)}>
                                                    <FontAwesomeIcon icon={faEdit} className="me-2" /> Edit
                                                </Dropdown.Item>
                                                <Dropdown.Item onClick={() => handleIssueInvoice(invoice.id)} disabled={actionLoading === invoice.id}>
                                                    <FontAwesomeIcon icon={faFileInvoice} className="me-2" /> Issue
                                                </Dropdown.Item>
                                                <Dropdown.Divider />
                                                <Dropdown.Item onClick={() => setConfirmDelete(invoice.id)} className="text-danger">
                                                    <FontAwesomeIcon icon={faTrash} className="me-2" /> Delete
                                                </Dropdown.Item>
                                            </>
                                        )}

                                        {['ISSUED', 'OVERDUE'].includes(invoice.status) && (
                                            <Dropdown.Item onClick={() => handleCancelInvoice(invoice.id)} disabled={actionLoading === invoice.id}>
                                                <FontAwesomeIcon icon={faBan} className="me-2" /> Cancel
                                            </Dropdown.Item>
                                        )}

                                        {invoice.status === 'PAID' && (
                                            <Dropdown.Item disabled>No actions available</Dropdown.Item>
                                        )}

                                        {invoice.status === 'CANCELLED' && (
                                            <Dropdown.Item disabled>No actions available</Dropdown.Item>
                                        )}
                                    </Dropdown.Menu>
                                </Dropdown>

                                {actionLoading === invoice.id && (
                                    <Spinner animation="border" size="sm" className="ms-2" />
                                )}
                            </td>
                        </tr>
                    ))
                )}
                </tbody>
            </Table>

            <div className="d-flex justify-content-between align-items-center">
                <div>
                    Showing {invoices.length} of {totalPages * size} entries
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
                                Are you sure you want to delete this invoice? This action cannot be undone.
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

export default InvoiceList;