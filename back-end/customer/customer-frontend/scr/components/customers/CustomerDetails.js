// src/components/customers/CustomerDetails.js

import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getCustomerById, deleteCustomer, changeCustomerStatus } from '../../services/customerService';
import { formatDate } from '../../utils/formatters';
import Loading from '../common/Loading';
import Alert from '../common/Alert';

const CustomerDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [customer, setCustomer] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchCustomerDetails = async () => {
            try {
                setLoading(true);
                const data = await getCustomerById(id);
                setCustomer(data);
                setLoading(false);
            } catch (err) {
                setError('Erro ao carregar informações do cliente: ' + (err.message || 'Erro desconhecido'));
                setLoading(false);
            }
        };

        fetchCustomerDetails();
    }, [id]);

    const handleDelete = async () => {
        if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
            try {
                setLoading(true);
                await deleteCustomer(id);
                navigate('/customers', { state: { message: 'Cliente excluído com sucesso' } });
            } catch (err) {
                setError('Erro ao excluir cliente: ' + (err.message || 'Erro desconhecido'));
                setLoading(false);
            }
        }
    };

    const handleStatusChange = async (newStatus) => {
        try {
            setLoading(true);
            await changeCustomerStatus(id, newStatus);
            const updatedCustomer = await getCustomerById(id);
            setCustomer(updatedCustomer);
            setLoading(false);
        } catch (err) {
            setError('Erro ao alterar status: ' + (err.message || 'Erro desconhecido'));
            setLoading(false);
        }
    };

    if (loading) {
        return <Loading />;
    }

    if (error) {
        return <Alert type="danger" message={error} />;
    }

    if (!customer) {
        return <Alert type="warning" message="Cliente não encontrado" />;
    }

    return (
        <div className="container mt-4">
            <h2>Detalhes do Cliente</h2>

            <div className="card">
                <div className="card-header d-flex justify-content-between align-items-center">
                    <h5 className="mb-0">{customer.name}</h5>
                    <span className={`badge ${customer.status === 'ACTIVE' ? 'bg-success' :
                        customer.status === 'INACTIVE' ? 'bg-warning' : 'bg-danger'}`}>
            {customer.status === 'ACTIVE' ? 'Ativo' :
                customer.status === 'INACTIVE' ? 'Inativo' : 'Bloqueado'}
          </span>
                </div>

                <div className="card-body">
                    <div className="row mb-3">
                        <div className="col-md-6">
                            <h6>Informações Gerais</h6>
                            <ul className="list-group list-group-flush">
                                <li className="list-group-item">
                                    <strong>CNPJ:</strong> {customer.cnpj}
                                </li>
                                <li className="list-group-item">
                                    <strong>Email:</strong> {customer.email}
                                </li>
                                <li className="list-group-item">
                                    <strong>Telefone:</strong> {customer.phone}
                                </li>
                            </ul>
                        </div>

                        <div className="col-md-6">
                            <h6>Endereço</h6>
                            <ul className="list-group list-group-flush">
                                <li className="list-group-item">
                                    <strong>Logradouro:</strong> {customer.address}
                                </li>
                                <li className="list-group-item">
                                    <strong>Cidade/UF:</strong> {customer.city}/{customer.state}
                                </li>
                                <li className="list-group-item">
                                    <strong>CEP:</strong> {customer.zipCode}
                                </li>
                            </ul>
                        </div>
                    </div>

                    {customer.notes && (
                        <div className="mb-3">
                            <h6>Observações</h6>
                            <p>{customer.notes}</p>
                        </div>
                    )}

                    <div className="row">
                        <div className="col-md-6">
                            <small className="text-muted">
                                Criado em: {formatDate(customer.createdAt)}
                            </small>
                        </div>
                        <div className="col-md-6">
                            <small className="text-muted">
                                Última atualização: {formatDate(customer.updatedAt)}
                            </small>
                        </div>
                    </div>
                </div>

                <div className="card-footer">
                    <div className="btn-group">
                        <Link to="/customers" className="btn btn-secondary">
                            <i className="bi bi-arrow-left"></i> Voltar
                        </Link>
                        <Link to={`/customers/${id}/edit`} className="btn btn-primary">
                            <i className="bi bi-pencil"></i> Editar
                        </Link>
                        <button className="btn btn-danger" onClick={handleDelete}>
                            <i className="bi bi-trash"></i> Excluir
                        </button>

                        <div className="dropdown">
                            <button
                                className="btn btn-info dropdown-toggle"
                                type="button"
                                data-bs-toggle="dropdown"
                            >
                                Alterar Status
                            </button>
                            <ul className="dropdown-menu">
                                <li>
                                    <button
                                        className="dropdown-item"
                                        onClick={() => handleStatusChange('ACTIVE')}
                                        disabled={customer.status === 'ACTIVE'}
                                    >
                                        Ativar
                                    </button>
                                </li>
                                <li>
                                    <button
                                        className="dropdown-item"
                                        onClick={() => handleStatusChange('INACTIVE')}
                                        disabled={customer.status === 'INACTIVE'}
                                    >
                                        Inativar
                                    </button>
                                </li>
                                <li>
                                    <button
                                        className="dropdown-item"
                                        onClick={() => handleStatusChange('BLOCKED')}
                                        disabled={customer.status === 'BLOCKED'}
                                    >
                                        Bloquear
                                    </button>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CustomerDetails;