// src/components/timetracking/TimeEntryList.jsx
import React, { useState, useEffect } from 'react';
import { Table, Button, Card, Form, Row, Col, Badge } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEdit, faTrash, faClockRotateLeft, faClock, faPlus } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';
import { timeTrackingService } from '../../services/timeTrackingService';
import { employeeService } from '../../services/employeeService';
import Loader from '../common/Loader';
import { format } from 'date-fns';
import { toast } from 'react-toastify';

const TimeEntryList = () => {
    const [timeEntries, setTimeEntries] = useState([]);
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [employeeFilter, setEmployeeFilter] = useState('');
    const [dateFilter, setDateFilter] = useState({
        startDate: format(new Date(new Date().setDate(1)), 'yyyy-MM-dd'),
        endDate: format(new Date(), 'yyyy-MM-dd')
    });
    const navigate = useNavigate();

    useEffect(() => {
        loadData();
    }, [employeeFilter, dateFilter]);

    const loadData = async () => {
        setLoading(true);
        try {
            // Load employees for filter
            const employeeData = await employeeService.getActiveEmployees();
            setEmployees(employeeData);

            // Load time entries based on filters
            let timeEntryData;

            if (employeeFilter) {
                timeEntryData = await timeTrackingService.getTimeEntriesByEmployeeId(employeeFilter);
            } else {
                const start = new Date(`${dateFilter.startDate}T00:00:00`);
                const end = new Date(`${dateFilter.endDate}T23:59:59`);
                timeEntryData = await timeTrackingService.getTimeEntriesByDateRange(start, end);
            }

            setTimeEntries(timeEntryData.sort((a, b) =>
                new Date(b.entryTime) - new Date(a.entryTime)
            ));
        } catch (error) {
            toast.error('Failed to load time entries');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteEntry = async (id) => {
        if (window.confirm('Are you sure you want to delete this time entry?')) {
            try {
                await timeTrackingService.deleteTimeEntry(id);
                toast.success('Time entry deleted successfully');
                loadData();
            } catch (error) {
                toast.error('Failed to delete time entry');
            }
        }
    };

    const handleRecordExit = async (id) => {
        try {
            const notes = prompt('Please enter any notes about the work done (optional):');
            await timeTrackingService.recordExit(id, notes || '');
            toast.success('Exit time recorded successfully');
            loadData();
        } catch (error) {
            toast.error('Failed to record exit time');
        }
    };

    const handleFilterChange = (e) => {
        const { name, value } = e.target;

        if (name === 'employeeFilter') {
            setEmployeeFilter(value);
        } else {
            setDateFilter(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    const getEntryTypeBadge = (entryType) => {
        const badges = {
            REGULAR: 'primary',
            OVERTIME: 'warning',
            REMOTE: 'info',
            ABSENCE: 'danger',
            VACATION: 'success',
            SICK_LEAVE: 'secondary'
        };

        const label = entryType.replace('_', ' ').toLowerCase();

        return (
            <Badge bg={badges[entryType] || 'primary'}>
                {label.charAt(0).toUpperCase() + label.slice(1)}
            </Badge>
        );
    };

    const formatDateTime = (dateTimeString) => {
        const date = new Date(dateTimeString);
        return format(date, 'MMM dd, yyyy HH:mm:ss');
    };

    const calculateDuration = (entryTime, exitTime) => {
        if (!exitTime) return 'Open';

        const entry = new Date(entryTime);
        const exit = new Date(exitTime);
        const diffMs = exit - entry;

        const hours = Math.floor(diffMs / (1000 * 60 * 60));
        const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

        return `${hours}h ${minutes}m`;
    };

    return (
        <Card className="shadow">
            <Card.Header className="bg-primary text-white d-flex justify-content-between align-items-center">
                <h5 className="mb-0">Time Tracking</h5>
                <Button
                    variant="light"
                    size="sm"
                    onClick={() => navigate('/time-tracking/new')}
                >
                    <FontAwesomeIcon icon={faPlus} /> Record Entry
                </Button>
            </Card.Header>
            <Card.Body>
                <Form className="mb-4">
                    <Row>
                        <Col md={4}>
                            <Form.Group className="mb-3">
                                <Form.Label>Filter by Employee</Form.Label>
                                <Form.Select
                                    name="employeeFilter"
                                    value={employeeFilter}
                                    onChange={handleFilterChange}
                                >
                                    <option value="">All Employees</option>
                                    {employees.map((employee) => (
                                        <option key={employee.id} value={employee.id}>
                                            {employee.name}
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>

                        <Col md={4}>
                            <Form.Group className="mb-3">
                                <Form.Label>Start Date</Form.Label>
                                <Form.Control
                                    type="date"
                                    name="startDate"
                                    value={dateFilter.startDate}
                                    onChange={handleFilterChange}
                                />
                            </Form.Group>
                        </Col>

                        <Col md={4}>
                            <Form.Group className="mb-3">
                                <Form.Label>End Date</Form.Label>
                                <Form.Control
                                    type="date"
                                    name="endDate"
                                    value={dateFilter.endDate}
                                    onChange={handleFilterChange}
                                />
                            </Form.Group>
                        </Col>
                    </Row>
                </Form>

                {loading ? (
                    <Loader />
                ) : (
                    <Table striped bordered hover responsive>
                        <thead>
                        <tr>
                            <th>Employee</th>
                            <th>Entry Time</th>
                            <th>Exit Time</th>
                            <th>Duration</th>
                            <th>Type</th>
                            <th>Notes</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {timeEntries.length > 0 ? (
                            timeEntries.map((entry) => (
                                <tr key={entry.id}>
                                    <td>{entry.employeeName}</td>
                                    <td>{formatDateTime(entry.entryTime)}</td>
                                    <td>
                                        {entry.exitTime
                                            ? formatDateTime(entry.exitTime)
                                            : <span className="text-warning">Open</span>}
                                    </td>
                                    <td>{calculateDuration(entry.entryTime, entry.exitTime)}</td>
                                    <td>{getEntryTypeBadge(entry.entryType)}</td>
                                    <td>{entry.notes}</td>
                                    <td>
                                        {!entry.exitTime && (
                                            <Button
                                                variant="outline-success"
                                                size="sm"
                                                className="me-2"
                                                onClick={() => handleRecordExit(entry.id)}
                                                title="Record exit time"
                                            >
                                                <FontAwesomeIcon icon={faClock} />
                                            </Button>
                                        )}

                                        <Button
                                            variant="outline-primary"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => navigate(`/time-tracking/${entry.id}`)}
                                            title="Edit entry"
                                        >
                                            <FontAwesomeIcon icon={faEdit} />
                                        </Button>

                                        <Button
                                            variant="outline-danger"
                                            size="sm"
                                            onClick={() => handleDeleteEntry(entry.id)}
                                            title="Delete entry"
                                        >
                                            <FontAwesomeIcon icon={faTrash} />
                                        </Button>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="7" className="text-center">
                                    No time entries found
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </Table>
                )}
            </Card.Body>
        </Card>
    );
};

export default TimeEntryList;