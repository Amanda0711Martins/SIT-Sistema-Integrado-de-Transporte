// src/utils/formatters.js

// Formata data para exibição
export const formatDate = (dateString) => {
    if (!dateString) return 'N/A';

    const options = {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    };

    return new Date(dateString).toLocaleDateString('pt-BR', options);
};

// Formata CNPJ para exibição
export const formatCNPJ = (cnpj) => {
    if (!cnpj) return '';

    // Se já está formatado, retorna como está
    if (/^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/.test(cnpj)) {
        return cnpj;
    }

    // Se está sem formatação, adiciona
    if (/^\d{14}$/.test(cnpj)) {
        return cnpj.replace(
            /^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/,
            '$1.$2.$3/$4-$5'
        );
    }

    return cnpj;
};

// Formata telefone para exibição
export const formatPhone = (phone) => {
    if (!phone) return '';

    // Se já está formatado, retorna como está
    if (/^\(\d{2}\)\s\d{4,5}-\d{4}$/.test(phone)) {
        return phone;
    }

    // Se está sem formatação, adiciona
    const cleaned = phone.replace(/\D/g, '');

    if (cleaned.length === 10) {
        return cleaned.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3');
    } else if (cleaned.length === 11) {
        return cleaned.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3');
    }

    return phone;
};

// Formata CEP para exibição
export const formatCEP = (cep) => {
    if (!cep) return '';

    // Se já está formatado, retorna como está
    if (/^\d{5}-\d{3}$/.test(cep)) {
        return cep;
    }

    // Se está sem formatação, adiciona
    const cleaned = cep.replace(/\D/g, '');

    if (cleaned.length === 8) {
        return cleaned.replace(/^(\d{5})(\d{3})$/, '$1-$2');
    }

    return cep;
};