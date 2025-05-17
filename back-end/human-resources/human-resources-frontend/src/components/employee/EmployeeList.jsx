// src/components/employee/EmployeeList.jsx
import React, { useState, useEffect } from 'react';
import { Table, Button, Pagination, Card, Form, InputGroup } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEdit, faTrash, faSearch, faUserPlus } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';
import { employeeService } from '../../services/employeeService';
import Loader from '../common/Loader';
import { formatCurrency } from '../../utils/formatter';
import { toast } from 'react-toastify';

const EmployeeList = () => {
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [totalPages, setTotalPages] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize] = useState(10);
    const [searchTerm, setSearchTerm] = useState('');
    const [departmentFilter, setDepartmentFilter] = useState('');
    const [departments, setDepartments] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        loadEmployees();
    }, [currentPage, departmentFilter]);

    const loadEmployees = async () => {
        setLoading(true);
        try {
            let data;

            if (departmentFilter) {
                data = await employeeService.getEmployeesByDepartment(departmentFilter);
                setEmployees(data);
                setTotalPages(Math.ceil(data.length / pageSize));
            } else {
                const response = await employeeService.getAllEmployees({
                    page: currentPage,
                    size: pageSize,
                    sort: 'name,asc'
                });
                setEmployees(response.content);
                setTotalPages(response.totalPages);
            }

            // Extract unique departments for filtering
            const allEmployees = await employeeService.getAllEmployees({ size: 1000 });
            const uniqueDepartments = [...new Set(allEmployees.content
                .map(emp => emp.department)
                .filter(Boolean))];
            setDepartments(uniqueDepartments);
        } catch (error) {
            toast.error('Failed to load employees');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteEmployee = async (id) => {
        if (window.confirm('Are you sure you want to delete this employee?')) {
            try {
                await employeeService.deleteEmployee(id);
                toast.success('Employee deleted successfully');
                loadEmployees();
            } catch (error) {
                toast.error('Failed to delete employee');
            }
        }
    };

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const filteredEmployees = employees.filter(employee =>
        employee.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        employee.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        employee.position.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <Card className="shadow">
            <Card.Header className="bg-primary text-white d-flex justify-content-between align-items-center">
                <h5 className="mb-0">Employee Management</h5>
                <Button
                    variant="light"
                    size="sm"
                    onClick={() => navigate('/employees/new')}
                >
                    <FontAwesomeIcon icon={faUserPlus} /> Add Employee
                </Button>
            </Card.Header>
            <Card.Body>
                <div className="d-flex justify-content-between mb-3">
                    <InputGroup className="w-50">
                        <InputGroup.Text>
                            <FontAwesomeIcon icon={faSearch} />
                        </InputGroup.Text>
                        <Form.Control
                            placeholder="Search employees..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </InputGroup>

                    <Form.Select
                        className="w-25"
                        value={departmentFilter}
                        onChange={(e) => setDepartmentFilter(e.target.value)}
                    >
                        <option value="">All Departments</option>
                        {departments.map((dept, idx) => (
                            <option key={idx} value={dept}>{dept}</option>
                        ))}
                    </Form.Select>
                </div>

                {loading ? (
                    <Loader />
                ) : (
                    <>
                        <Table striped bordered hover responsive>
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Position</th>
                                <th>Department</th>
                                <th>Base Salary</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {filteredEmployees.length > 0 ? (
                                filteredEmployees.map((employee) => (
                                    <tr key={employee.id}>
                                        <td>{employee.name}</td>
                                        <td>{employee.email}</td>
                                        <td>{employee.position}</td>
                                        <td>{employee.department}</td>
                                        <td>{formatCurrency(employee.baseSalary)}</td>
                                        <td>
                        <span className={`badge bg-${employee.active ? 'success' : 'danger'}`}>
                          {employee.active ? 'Active' : 'Inactive'}
                        </span>
                                        </td>
                                        <td>
                                            <Button
                                                variant="outline-primary"
                                                size="sm"
                                                className="me-2"
                                                onClick={() => navigate(`/employees/${employee.id}`)}
                                            >
                                                <FontAwesomeIcon icon={faEdit} />
                                            </Button>
                                            <Button
                                                variant="outline-danger"
                                                size="sm"
                                                onClick={() => handleDeleteEmployee(employee.id)}
                                            >
                                                <FontAwesomeIcon icon={faTrash} />
                                            </Button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="7" className="text-center">
                                        No employees found
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </Table>

                        {!departmentFilter && totalPages > 1 && (
                            <div className="d-flex justify-content-center">
                                <Pagination>
                                    <Pagination.First
                                        onClick={() => handlePageChange(0)}
                                        disabled={currentPage === 0}
                                    />
                                    <Pagination.Prev
                                        onClick={() => handlePageChange(currentPage - 1)}
                                        disabled={currentPage === 0}
                                    />

                                    {[...Array(totalPages).keys()].map((page) => (
                                        <Pagination.Item
                                            key={page}
                                            active={page === currentPage}
                                            onClick={() => handlePageChange(page)}
                                        >
                                            {page + 1}
                                        </Pagination.Item>
                                    ))}

                                    <Pagination.Next
                                        onClick={() => handlePageChange(currentPage + 1)}
                                        disabled={currentPage === totalPages - 1}
                                    />
                                    <Pagination.Last
                                        onClick={() => handlePageChange(totalPages - 1)}
                                        disabled={currentPage === totalPages - 1}
                                    />
                                </Pagination>
                            </div>
                        )}
                    </>
                )}
            </Card.Body>
        </Card>
    );
};

export default EmployeeList;