import React, { useState, useEffect } from 'react';
import { Container, Grid, Typography, Paper, Box } from '@mui/material';
import FreightForm from '../components/freight/FreightForm';
import FreightHistory from '../components/freight/FreightHistory';
import FreightCalculator from '../components/freight/FreightCalculator';
import { useAuth } from '../hooks/useAuth';
import Loading from '../components/common/Loading';
import ErrorBoundary from '../components/common/ErrorBoundary';

const FreightPage = () => {
    const { user, loading: authLoading } = useAuth();
    const [calculationResult, setCalculationResult] = useState(null);
    const [reloadHistory, setReloadHistory] = useState(0);

    // Handle completed calculation
    const handleCalculationComplete = (result) => {
        setCalculationResult(result);
        // Trigger reload of history component
        setReloadHistory(prev => prev + 1);
    };

    if (authLoading) {
        return <Loading />;
    }

    return (
        <ErrorBoundary>
            <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
                <Typography variant="h4" gutterBottom>
                    Gerenciamento de Fretes
                </Typography>

                <Grid container spacing={3}>
                    {/* Freight Form */}
                    <Grid item xs={12} md={6}>
                        <FreightForm onCalculationComplete={handleCalculationComplete} />
                    </Grid>

                    {/* Calculation Result */}
                    <Grid item xs={12} md={6}>
                        <FreightCalculator calculationResult={calculationResult} />
                    </Grid>

                    {/* Freight History */}
                    <Grid item xs={12}>
                        <Paper sx={{ p: 2 }}>
                            <Box sx={{ mb: 2 }}>
                                <Typography variant="h6" component="h3">
                                    Histórico de Cálculos
                                </Typography>
                            </Box>

                            <FreightHistory
                                clientId={user?.role === 'CLIENT' ? user.clientId : null}
                                reloadTrigger={reloadHistory}
                            />
                        </Paper>
                    </Grid>
                </Grid>
            </Container>
        </ErrorBoundary>
    );
};

export default FreightPage;