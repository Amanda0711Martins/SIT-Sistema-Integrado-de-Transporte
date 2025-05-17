// src/components/customers/CustomerFilters.js

import React, { useState } from 'react';

const CustomerFilters = ({ onFilterChange }) => {
    const [name, setName] = useState('');
    const [status, setStatus] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        onFilterChange({ name, status: status || null });
    };

    const handleClear = () => {
        setName('');
        setStatus('');
        onFilterChange({ name: '', status: null });
    };

    return (
        <div className="card mb-4">
            <div className="card-body">
                <h5 className="card-title">Filtros</h5>

                <form onSubmit={handleSubmit}>
                    <div className="row g-3">
                        <div className="col-md-5">
                            <label htmlFor="nameFilter" className="form-label">Nome</label>
                            <input
                                type="text"
                                className="form-control"
                                id="nameFilter"
                                placeholder="Filtrar por nome"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                            />
                        </div>

                        <div className="col-md-5">
                            <label htmlFor="statusFilter" className="form-label">Status</label>
                            <select
                                className="form-select"
                                id="statusFilter"
                                value={status}
                                onChange={(e) => setStatus(e.target.value)}
                            >
                                <option value="">Todos</option>
                                <option value="ACTIVE">Ativos</option>
                                <option value="INACTIVE">Inativos</option>
                                <option value="BLOCKED">Bloqueados</option>
                            </select>
                        </div>

                        <div className="col-md-2 d-flex align-items-end">
                            <div className="d-grid gap-2">
                                <button type="submit" className="btn btn-primary">
                                    <i className="bi bi-search"></i> Filtrar
                                </button>
                                <button type="button" className="btn btn-outline-secondary" onClick={handleClear}>
                                    <i className="bi bi-x-circle"></i> Limpar
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CustomerFilters;