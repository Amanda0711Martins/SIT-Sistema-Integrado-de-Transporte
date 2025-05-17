import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import FreightCalculator from '../../components/freight/FreightCalculator';

describe('FreightCalculator', () => {
    const mockCalculationResult = {
        id: 1,
        clientId: 123,
        origin: 'São Paulo',
        destination: 'Rio de Janeiro',
        distance: 429.5,
        weight: 100,
        volume: 2,
        productType: 'Electronics',
        rush: true,
        fragile: false,
        insurance: true,
        baseValue: 250.0,
        totalValue: 343.75,
        calculatedAt: '2023-04-10T14:30:00'
    };

    test('renders no calculation message when no result is provided', () => {
        render(<FreightCalculator />);

        expect(screen.getByText(/Nenhum cálculo de frete/i)).toBeInTheDocument();
    });

    test('renders freight calculation details when result is provided', () => {
        render(<FreightCalculator calculationResult={mockCalculationResult} />);

        expect(screen.getByText(/Resultado do Cálculo de Frete/i)).toBeInTheDocument();
        expect(screen.getByText(/São Paulo/i)).toBeInTheDocument();
        expect(screen.getByText(/Rio de Janeiro/i)).toBeInTheDocument();
        expect(screen.getByText(/429.5 km/i)).toBeInTheDocument();
        expect(screen.getByText(/R\$ 343,75/i)).toBeInTheDocument();
    });

    test('displays services selected correctly', () => {
        render(<FreightCalculator calculationResult={mockCalculationResult} />);

        // Rush is true
        expect(screen.getByText(/Entrega Urgente/i)).toBeInTheDocument();

        // Fragile is false, shouldn't appear
        expect(screen.queryByText(/Produto Frágil/i)).not.toBeInTheDocument();

        // Insurance is true
        expect(screen.getByText(/Seguro de Carga/i)).toBeInTheDocument();
    });
});