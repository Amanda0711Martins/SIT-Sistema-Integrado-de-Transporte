// src/components/employee/EmployeeForm.jsx
import React, { useState, useEffect } from 'react';
import { Card, Form, Button, Row, Col } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { employeeService } from '../../services/employeeService';
import Loader from '../common/Loader';
import { toast } from 'react-toastify';
import * as Yup from 'yup';
import { Formik } from 'formik';
import { format } from 'date-fns';

const EmployeeForm = () => {
    const [employee, setEmployee] = useState(null);
    const [loading, setLoading] = useState(false);
    const { id } = useParams();
    const navigate = useNavigate();
    const isEditMode = id !== 'new';

    useEffect(() => {
        if (isEditMode) {
            loadEmployee();
        }
    }, [id]);

    const loadEmployee = async () => {
        setLoading(true);
        try {
            const data = await employeeService.getEmployeeById(id);
            setEmployee(data);
        } catch (error) {
            toast.error('Failed to load employee data');
            navigate('/employees');
        } finally {
            setLoading(false);
        }
    };

    const validationSchema = Yup.object().shape({
        name: Yup.string()
            .required('Name is required')
            .min(3, 'Name must be at least 3 characters')
            .max(100, 'Name must not exceed 100 characters'),
        email: Yup.string()
            .email('Invalid email format')
            .required('Email is required'),
        position: Yup.string()
            .required('Position is required'),
        department: Yup.string()
            .required('Department is required'),
        baseSalary: Yup.number()
            .required('Base salary is required')
            .positive('Base salary must be positive'),
        hireDate: Yup.date()
            .required('Hire date is required')
            .max(new Date(), 'Hire date cannot be in the future'),
        birthDate: Yup.date()
            .required('Birth date is required')
            .max(new Date(), 'Birth date cannot be in the future')
            .test(
                'is-adult',
                'Employee must be at least 18 years old',
                function(value) {
                    if (!value) return true;
                    const today = new Date();
                    const birthDate = new Date(value);
                    let age = today.getFullYear() - birthDate.getFullYear();
                    const monthDiff = today.getMonth() - birthDate.getMonth();
                    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                        age--;
                    }
                    return age >= 18;
                }
            ),
        cpf: isEditMode
            ? Yup.string()
            : Yup.string()
                .required('CPF is required')
                .matches(/^\d{3}\.\d{3}\.\d{3}-\d{2}$|^\d{11}$/, 'Invalid CPF format'),
        active: Yup.boolean()
    });

    const initialValues = {
        name: employee?.name || '',
        email: employee?.email || '',
        cpf: '',  // We don't retrieve CPF from the API for security
        position: employee?.position || '',
        department: employee?.department || '',
        baseSalary: employee?.baseSalary || '',
        hireDate: employee?.hireDate ? format(new Date(employee.hireDate), 'yyyy-MM-dd') : '',
        birthDate: employee?.birthDate ? format(new Date(employee.birthDate), 'yyyy-MM-dd') : '',
        active: employee?.active !== undefined ? employee.active : true
    };

    const handleSubmit = async (values, { setSubmitting }) => {
        try {
            if (isEditMode) {
                await employeeService.updateEmployee(id, values);
                toast.success('Employee updated successfully');
            } else {
                await employeeService.createEmployee(values);
                toast.success('Employee created successfully');
            }
            navigate('/employees');
        } catch (error) {
            toast.error(`Failed to ${isEditMode ? 'update' : 'create'} employee`);
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <Loader />;

    return (
        <Card className="shadow">
            <Card.Header className="bg-primary text-white">
                <h5 className="mb-0">{isEditMode ? 'Edit Employee' : 'Add New Employee'}</h5>
            </Card.Header>
            <Card.Body>
                <Formik
                    initialValues={initialValues}
                    validationSchema={validationSchema}
                    onSubmit={handleSubmit}
                    enableReinitialize
                >
                    {({
                          values,
                          errors,
                          touched,
                          handleChange,
                          handleBlur,
                          handleSubmit,
                          isSubmitting,
                      }) => (
                        <Form onSubmit={handleSubmit}>
                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Full Name</Form.Label>
                                        <Form.Control
                                            type="text"
                                            name="name"
                                            value={values.name}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            isInvalid={touched.name && errors.name}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.name}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Email</Form.Label>
                                        <Form.Control
                                            type="email"
                                            name="email"
                                            value={values.email}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            isInvalid={touched.email && errors.email}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.email}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            {!isEditMode && (
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>CPF</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="cpf"
                                                placeholder="123.456.789-00"
                                                value={values.cpf}
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                isInvalid={touched.cpf && errors.cpf}
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                {errors.cpf}
                                            </Form.Control.Feedback>
                                            <Form.Text className="text-muted">
                                                CPF cannot be changed after creation for security reasons.
                                            </Form.Text>
                                        </Form.Group>
                                    </Col>
                                </Row>
                            )}

                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Position</Form.Label>
                                        <Form.Control
                                            type="text"
                                            name="position"
                                            value={values.position}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            isInvalid={touched.position && errors.position}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.position}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Department</Form.Label>
                                        <Form.Select
                                            name="department"
                                            value={values.department}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            isInvalid={touched.department && errors.department}
                                        >
                                            <option value="">Select Department</option>
                                            <option value="IT">IT</option>
                                            <option value="HR">HR</option>
                                            <option value="Finance">Finance</option>
                                            <option value="Operations">Operations</option>
                                            <option value="Marketing">Marketing</option>
                                            <option value="Sales">Sales</option>
                                        </Form.Select>
                                        <Form.Control.Feedback type="invalid">
                                            {errors.department}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Base Salary</Form.Label>
                                        <Form.Control
                                            type="number"
                                            name="baseSalary"
                                            step="0.01"
                                            value={values.baseSalary}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            isInvalid={touched.baseSalary && errors.baseSalary}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.baseSalary}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Status</Form.Label>
                                        <div>
                                            <Form.Check
                                                type="switch"
                                                id="active-switch"
                                                name="active"
                                                label={values.active ? "Active" : "Inactive"}
                                                checked={values.active}
                                                onChange={handleChange}
                                            />
                                        </div>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Hire Date</Form.Label>
                                        <Form.Control
                                            type="date"
                                            name="hireDate"
                                            value={values.hireDate}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            isInvalid={touched.hireDate && errors.hireDate}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.hireDate}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>Birth Date</Form.Label>
                                        <Form.Control
                                            type="date"
                                            name="birthDate"
                                            value={values.birthDate}
                                            onChange={handleChange}
                                            onBlur={handleBlur}
                                            isInvalid={touched.birthDate && errors.birthDate}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.birthDate}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <div className="d-flex justify-content-end gap-2 mt-4">
                                <Button
                                    variant="secondary"
                                    onClick={() => navigate('/employees')}
                                    disabled={isSubmitting}
                                >
                                    Cancel
                                </Button>
                                <Button
                                    type="submit"
                                    variant="primary"
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting ? 'Saving...' : isEditMode ? 'Update Employee' : 'Create Employee'}
                                </Button>
                            </div>
                        </Form>
                    )}
                </Formik>
            </Card.Body>
        </Card>
    );
};

export default EmployeeForm;