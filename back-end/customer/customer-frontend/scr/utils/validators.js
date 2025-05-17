// src/utils/validators.js

// Validação de CNPJ (formato XX.XXX.XXX/XXXX-XX)
export const validateCNPJ = (cnpj) => {
    const regex = /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/;
    return regex.test(cnpj);
};

// Validação de Email
export const validateEmail = (email) => {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
};

// Validação de Telefone (formato (XX) XXXX-XXXX ou (XX) XXXXX-XXXX)
export const validatePhone = (phone) => {
    const regex = /^\(\d{2}\)\s\d{4,5}-\d{4}$/;
    return regex.test(phone);
};

// Validação de CEP (formato XXXXX-XXX)
export const validateCEP = (cep) => {
    const regex = /^\d{5}-\d{3}$/;
    return regex.test(cep);
};