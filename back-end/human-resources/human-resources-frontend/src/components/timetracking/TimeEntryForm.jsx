// src/components/timetracking/TimeEntryForm.jsx
import React, { useState, useEffect } from 'react';
import { Card, Form, Button, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { employeeService } from '../../services/employeeService';
import { timeTrackingService } from '../../services/timeTrackingService';
import { toast } from 'react-toastify';
import Loader from '../common/Loader';

const TimeEntryForm = () => {
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [formData, setFormData] = useState({
        employeeId: '',
        entryType: 'REGULAR',
        notes: ''
    });
    const navigate = useNavigate();

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
        setSubmitting(true);

        try {
            if (!formData.employeeId) {
                toast.error('Please select an employee');
                return;
            }

            const timeEntry = {
                ...formData,
                entryTime: new Date().toISOString()
            };

            await timeTrackingService.recordEntry(timeEntry);
            toast.success('Time entry recorded successfully');
            navigate('/time-tracking');
        } catch (error) {
            toast.error('Failed to record time entry');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <Loader />;

    return (
        <Card className="shadow">
            <Card.Header className="bg-primary text-white">
                <h5 className="mb-0">Record Time Entry</h5>
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

                        <Col md={6}>
                            <Form.Group className="mb-3">
                                <Form.Label>Entry Type</Form.Label>
                                <Form.Select
                                    name="entryType"
                                    value={formData.entryType}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="REGULAR">Regular</option>
                                    <option value="OVERTIME">Overtime</option>
                                    <option value="REMOTE">Remote Work</option>
                                    <option value="ABSENCE">Absence</option>
                                    <option value="VACATION">Vacation</option>
                                    <option value="SICK_LEAVE">Sick Leave</option>
                                </Form.Select>
                            </Form.Group>
                        </Col>
                    </Row>

                    <Form.Group className="mb-3">
                        <Form.Label>Notes</Form.Label>
                        <Form.Control
                            as="textarea"
                            name="notes"
                            value={formData.notes}
                            onChange={handleChange}
                            placeholder="Any additional information..."
                            rows={3}
                        />
                    </Form.Group>

                    <div className="d-flex justify-content-end gap-2 mt-4">
                        <Button
                            variant="secondary"
                            onClick={() => navigate('/time-tracking')}
                            disabled={submitting}
                        >
                            Cancel
                        </Button>
                        <Button
                            type="submit"
                            variant="primary"
                            disabled={submitting}
                        >
                            {submitting ? 'Recording...' : 'Record Entry'}
                        </Button>
                    </div>
                </Form>
            </Card.Body>
        </Card>
    );
};

export default TimeEntryForm;