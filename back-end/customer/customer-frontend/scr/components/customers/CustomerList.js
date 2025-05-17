// src/components/customers/CustomerList.js

import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { fetchCustomers, deleteCustomer, changeCustomerStatus } from '../../services/customerService';
import Pagination from '../common/Pagination';
import CustomerFilters from './CustomerFilters';
import Loading from '../common/Loading';
import Alert from '../common/Alert';

const CustomerList = () => {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [sortBy, setSortBy] = useState('name');
    const [sortDirection, setSortDirection] = useState('ASC');
    const [nameFilter, setNameFilter] = useState('');
    const [statusFilter, setStatusFilter] = useState('');
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    useEffect(() => {
        const loadCustomers = async () => {
            try {
                setLoading(true);
                setError(null);

                let response;
                if (nameFilter || statusFilter) {
                    response = await fetchCustomers({
                        page: currentPage,
                        size: pageSize,
                        name: nameFilter,
                        status: statusFilter
                    });
                } else {
                    response = await fetchCustomers({
                        page: currentPage,
                        size: pageSize,
                        sortBy,
                        sortDirection
                    });
                }

                setCustomers(response.content);
                setTotalElements(response.totalElements);
                setTotalPages(response.totalPages);
                setLoading(false);
            } catch (err) {
                setError('Erro ao carregar clientes: ' + (err.message || 'Erro desconhecido'));
                setLoading(false);
            }
        };

        loadCustomers();
    }, [currentPage, pageSize, sortBy, sortDirection, nameFilter, statusFilter, refreshTrigger]);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleSortChange = (field) => {
        if (field === sortBy) {
            setSortDirection(sortDirection === 'ASC' ? 'DESC' : 'ASC');
        } else {
            setSortBy(field);
            setSortDirection('ASC');
        }
    };

    const handleFiltersChange = (filters) => {
        setNameFilter(filters.name || '');
        setStatusFilter(filters.status || '');
        setCurrentPage(0); // Reset to first page when filters change
    };

    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
            try {
                setError(null);
                await deleteCustomer(id);
                setRefreshTrigger(prev => prev + 1); // Trigger a refresh
            } catch (err) {
                setError('Erro ao excluir cliente: ' + (err.message || 'Erro desconhecido'));
            }
        }
    };

    const handleStatusChange = async (id, newStatus) => {
        try {
            setError(null);
            await changeCustomerStatus(id, newStatus);
            setRefreshTrigger(prev => prev + 1); // Trigger a refresh
        } catch (err) {
            setError('Erro ao alterar status: ' + (err.message || 'Erro desconhecido'));
        }
    };

    return (
        <div className="container mt-4">
            <h2>Lista de Clientes</h2>

            <div className="mb-3">
                <Link to="/customers/new" className="btn btn-primary">
                    <i className="bi bi-plus-circle"></i> Novo Cliente
                </Link>
            </div>

            <CustomerFilters onFilterChange={handleFiltersChange} />

            {error && <Alert type="danger" message={error} />}

            {loading ? (
                <Loading />
            ) : (
                <>
                    <div className="table-responsive">
                        <table className="table table-striped table-hover">
                            <thead>
                            <tr>
                                <th onClick={() => handleSortChange('name')} style={{ cursor: 'pointer' }}>
                                    Nome {sortBy === 'name' && (sortDirection === 'ASC' ? '↑' : '↓')}
                                </th>
                                <th onClick={() => handleSortChange('cnpj')} style={{ cursor: 'pointer' }}>
                                    CNPJ {sortBy === 'cnpj' && (sortDirection === 'ASC' ? '↑' : '↓')}
                                </th>
                                <th>Email</th>
                                <th>Telefone</th>
                                <th onClick={() => handleSortChange('status')} style={{ cursor: 'pointer' }}>
                                    Status {sortBy === 'status' && (sortDirection === 'ASC' ? '↑' : '↓')}
                                </th>
                                <th>Ações</th>
                            </tr>
                            </thead>
                            <tbody>
                            {customers.length > 0 ? (
                                customers.map(customer => (
                                    <tr key={customer.id}>
                                        <td>{customer.name}</td>
                                        <td>{customer.cnpj}</td>
                                        <td>{customer.email}</td>
                                        <td>{customer.phone}</td>
                                        <td>
                        <span className={`badge ${customer.status === 'ACTIVE' ? 'bg-success' :
                            customer.status === 'INACTIVE' ? 'bg-warning' : 'bg-danger'}`}>
                          {customer.status === 'ACTIVE' ? 'Ativo' :
                              customer.status === 'INACTIVE' ? 'Inativo' : 'Bloqueado'}
                        </span>
                                        </td>
                                        <td>
                                            <div className="btn-group btn-group-sm">
                                                <Link to={`/customers/${customer.id}`} className="btn btn-info">
                                                    <i className="bi bi-eye"></i>
                                                </Link>
                                                <Link to={`/customers/${customer.id}/edit`} className="btn btn-primary">
                                                    <i className="bi bi-pencil"></i>
                                                </Link>
                                                <button
                                                    className="btn btn-danger"
                                                    onClick={() => handleDelete(customer.id)}
                                                >
                                                    <i className="bi bi-trash"></i>
                                                </button>
                                                <div className="dropdown">
                                                    <button
                                                        className="btn btn-secondary dropdown-toggle"
                                                        type="button"
                                                        data-bs-toggle="dropdown"
                                                    >
                                                        Status
                                                    </button>
                                                    <ul className="dropdown-menu">
                                                        <li>
                                                            <button
                                                                className="dropdown-item"
                                                                onClick={() => handleStatusChange(customer.id, 'ACTIVE')}
                                                                disabled={customer.status === 'ACTIVE'}
                                                            >
                                                                Ativar
                                                            </button>
                                                        </li>
                                                        <li>
                                                            <button
                                                                className="dropdown-item"
                                                                onClick={() => handleStatusChange(customer.id, 'INACTIVE')}
                                                                disabled={customer.status === 'INACTIVE'}
                                                            >
                                                                Inativar
                                                            </button>
                                                        </li>
                                                        <li>
                                                            <button
                                                                className="dropdown-item"
                                                                onClick={() => handleStatusChange(customer.id, 'BLOCKED')}
                                                                disabled={customer.status === 'BLOCKED'}
                                                            >
                                                                Bloquear
                                                            </button>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="6" className="text-center">Nenhum cliente encontrado</td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>

                    <Pagination
                        currentPage={currentPage}
                        totalPages={totalPages}
                        onPageChange={handlePageChange}
                    />

                    <div className="mt-3">
                        <small>Total de registros: {totalElements}</small>
                    </div>
                </>
            )}
        </div>
    );
};

export default CustomerList;