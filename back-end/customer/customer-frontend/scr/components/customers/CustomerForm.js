// src/components/customers/CustomerForm.js

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getCustomerById, createCustomer, updateCustomer } from '../../services/customerService';
import { validateCNPJ, validateEmail, validatePhone, validateCEP } from '../../utils/validators';
import Loading from '../common/Loading';
import Alert from '../common/Alert';

const CustomerForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEditMode = !!id;

    const [formData, setFormData] = useState({
        name: '',
        cnpj: '',
        email: '',
        phone: '',
        address: '',
        city: '',
        state: '',
        zipCode: '',
        notes: '',
        status: 'ACTIVE'
    });

    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    useEffect(() => {
        if (isEditMode) {
            const fetchCustomer = async () => {
                try {
                    setLoading(true);
                    const data = await getCustomerById(id);
                    setFormData(data);
                    setLoading(false);
                } catch (err) {
                    setError('Erro ao carregar informações do cliente: ' + (err.message || 'Erro desconhecido'));
                    setLoading(false);
                }
            };

            fetchCustomer();
        }
    }, [id, isEditMode]);

    const validateForm = () => {
        let formErrors = {};
        let isValid = true;

        if (!formData.name || formData.name.trim().length < 3) {
            formErrors.name = 'Nome deve ter pelo menos 3 caracteres';
            isValid = false;
        }

        if (!formData.cnpj || !validateCNPJ(formData.cnpj)) {
            formErrors.cnpj = 'CNPJ inválido (formato: XX.XXX.XXX/XXXX-XX)';
            isValid = false;
        }

        if (!formData.email || !validateEmail(formData.email)) {
            formErrors.email = 'Email inválido';
            isValid = false;
        }

        if (!formData.phone || !validatePhone(formData.phone)) {
            formErrors.phone = 'Telefone inválido (formato: (XX) XXXX-XXXX ou (XX) XXXXX-XXXX)';
            isValid = false;
        }

        if (!formData.address) {
            formErrors.address = 'Endereço é obrigatório';
            isValid = false;
        }

        if (!formData.city) {
            formErrors.city = 'Cidade é obrigatória';
            isValid = false;
        }

        if (!formData.state || formData.state.length !== 2) {
            formErrors.state = 'Estado deve ter 2 caracteres';
            isValid = false;
        }

        if (!formData.zipCode || !validateCEP(formData.zipCode)) {
            formErrors.zipCode = 'CEP inválido (formato: XXXXX-XXX)';
            isValid = false;
        }

        setErrors(formErrors);
        return isValid;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        // Limpar erro específico quando o campo for alterado
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: null
            }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        try {
            setSubmitting(true);
            setError(null);
            setSuccess(null);

            if (isEditMode) {
                await updateCustomer(id, formData);
                setSuccess('Cliente atualizado com sucesso!');
            } else {
                const newCustomer = await createCustomer(formData);
                setSuccess('Cliente cadastrado com sucesso!');
                setTimeout(() => {
                    navigate(`/customers/${newCustomer.id}`);
                }, 2000);
            }

            setSubmitting(false);
        } catch (err) {
            setError('Erro ao salvar cliente: ' + (err.response?.data?.message || err.message || 'Erro desconhecido'));
            setSubmitting(false);
        }
    };

    if (loading) {
        return <Loading />;
    }

    return (
        <div className="container mt-4">
            <h2>{isEditMode ? 'Editar Cliente' : 'Novo Cliente'}</h2>

            {error && <Alert type="danger" message={error} />}
            {success && <Alert type="success" message={success} />}

            <form onSubmit={handleSubmit}>
                <div className="row mb-3">
                    <div className="col-md-6">
                        <label htmlFor="name" className="form-label">Nome*</label>
                        <input
                            type="text"
                            className={`form-control ${errors.name ? 'is-invalid' : ''}`}
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="Nome da empresa"
                        />
                        {errors.name && <div className="invalid-feedback">{errors.name}</div>}
                    </div>

                    <div className="col-md-6">
                        <label htmlFor="cnpj" className="form-label">CNPJ*</label>
                        <input
                            type="text"
                            className={`form-control ${errors.cnpj ? 'is-invalid' : ''}`}
                            id="cnpj"
                            name="cnpj"
                            value={formData.cnpj}
                            onChange={handleChange}
                            placeholder="XX.XXX.XXX/XXXX-XX"
                        />
                        {errors.cnpj && <div className="invalid-feedback">{errors.cnpj}</div>}
                    </div>
                </div>

                <div className="row mb-3">
                    <div className="col-md-6">
                        <label htmlFor="email" className="form-label">Email*</label>
                        <input
                            type="email"
                            className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="contato@empresa.com.br"
                        />
                        {errors.email && <div className="invalid-feedback">{errors.email}</div>}
                    </div>

                    <div className="col-md-6">
                        <label htmlFor="phone" className="form-label">Telefone*</label>
                        <input
                            type="text"
                            className={`form-control ${errors.phone ? 'is-invalid' : ''}`}
                            id="phone"
                            name="phone"
                            value={formData.phone}
                            onChange={handleChange}
                            placeholder="(XX) XXXXX-XXXX"
                        />
                        {errors.phone && <div className="invalid-feedback">{errors.phone}</div>}
                    </div>
                </div>

                <div className="mb-3">
                    <label htmlFor="address" className="form-label">Endereço*</label>
                    <input
                        type="text"
                        className={`form-control ${errors.address ? 'is-invalid' : ''}`}
                        id="address"
                        name="address"
                        value={formData.address}
                        onChange={handleChange}
                        placeholder="Rua, número, complemento"
                    />
                    {errors.address && <div className="invalid-feedback">{errors.address}</div>}
                </div>

                <div className="row mb-3">
                    <div className="col-md-6">
                        <label htmlFor="city" className="form-label">Cidade*</label>
                        <input
                            type="text"
                            className={`form-control ${errors.city ? 'is-invalid' : ''}`}
                            id="city"
                            name="city"
                            value={formData.city}
                            onChange={handleChange}
                        />
                        {errors.city && <div className="invalid-feedback">{errors.city}</div>}
                    </div>

                    <div className="col-md-2">
                        <label htmlFor="state" className="form-label">Estado*</label>
                        <input
                            type="text"
                            className={`form-control ${errors.state ? 'is-invalid' : ''}`}
                            id="state"
                            name="state"
                            value={formData.state}
                            onChange={handleChange}
                            maxLength={2}
                            placeholder="UF"
                        />
                        {errors.state && <div className="invalid-feedback">{errors.state}</div>}
                    </div>

                    <div className="col-md-4">
                        <label htmlFor="zipCode" className="form-label">CEP*</label>
                        <input
                            type="text"
                            className={`form-control ${errors.zipCode ? 'is-invalid' : ''}`}
                            id="zipCode"
                            name="zipCode"
                            value={formData.zipCode}
                            onChange={handleChange}
                            placeholder="XXXXX-XXX"
                        />
                        {errors.zipCode && <div className="invalid-feedback">{errors.zipCode}</div>}
                    </div>
                </div>

                <div className="mb-3">
                    <label htmlFor="notes" className="form-label">Observações</label>
                    <textarea
                        className="form-control"
                        id="notes"
                        name="notes"
                        rows="3"
                        value={formData.notes}
                        onChange={handleChange}
                    ></textarea>
                </div>

                {isEditMode && (
                    <div className="mb-3">
                        <label htmlFor="status" className="form-label">Status</label>
                        <select
                            className="form-select"
                            id="status"
                            name="status"
                            value={formData.status}
                            onChange={handleChange}
                        >
                            <option value="ACTIVE">Ativo</option>
                            <option value="INACTIVE">Inativo</option>
                            <option value="BLOCKED">Bloqueado</option>
                        </select>
                    </div>
                )}

                <div className="mb-3">
                    <p><small>* Campos obrigatórios</small></p>
                </div>

                <div className="d-flex justify-content-between">
                    <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={() => navigate('/customers')}
                    >
                        Cancelar
                    </button>

                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={submitting}
                    >
                        {submitting ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                Salvando...
                            </>
                        ) : (
                            'Salvar'
                        )}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CustomerForm;