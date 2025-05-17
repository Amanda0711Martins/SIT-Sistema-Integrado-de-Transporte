import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
    Box,
    Button,
    Card,
    CardContent,
    Checkbox,
    FormControlLabel,
    Grid,
    TextField,
    Typography,
    CircularProgress
} from '@mui/material';
import freightService from '../../services/freightService';
import { useAuth } from '../../hooks/useAuth';
import { Alert } from '../common/Alert';

// Validation schema
const schema = yup.object().shape({
    clientId: yup.number().required('Cliente é obrigatório'),
    origin: yup.string().required('Origem é obrigatória'),
    destination: yup.string().required('Destino é obrigatório'),
    weight: yup.number()
        .positive('Peso deve ser maior que zero')
        .required('Peso é obrigatório'),
    volume: yup.number()
        .positive('Volume deve ser maior que zero')
        .required('Volume é obrigatório'),
    productType: yup.string(),
    rush: yup.boolean(),
    fragile: yup.boolean(),
    insurance: yup.boolean()
});

const FreightForm = ({ onCalculationComplete }) => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const { user } = useAuth();

    const { register, handleSubmit, formState: { errors }, reset, setValue } = useForm({
        resolver: yupResolver(schema),
        defaultValues: {
            clientId: '',
            origin: '',
            destination: '',
            weight: '',
            volume: '',
            productType: '',
            rush: false,
            fragile: false,
            insurance: false
        }
    });

    // Set the client ID automatically if user is a client
    useEffect(() => {
        if (user?.role === 'CLIENT' && user?.clientId) {
            setValue('clientId', user.clientId);
        }
    }, [user, setValue]);

    const onSubmit = async (data) => {
        try {
            setLoading(true);
            setError('');

            // Convert string values to numbers
            const payload = {
                ...data,
                clientId: Number(data.clientId),
                weight: Number(data.weight),
                volume: Number(data.volume)
            };

            const result = await freightService.calculateFreight(payload);

            // Notify parent component about successful calculation
            if (onCalculationComplete) {
                onCalculationComplete(result);
            }

            // Reset form
            reset();

        } catch (err) {
            setError(err.message || 'Erro ao calcular o frete. Tente novamente.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card>
            <CardContent>
                <Typography variant="h5" component="h2" gutterBottom>
                    Calculadora de Frete
                </Typography>

                {error && <Alert severity="error" message={error} />}

                <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate sx={{ mt: 2 }}>
                    <Grid container spacing={2}>
                        {/* Cliente ID */}
                        <Grid item xs={12} sm={6}>
                            <TextField
                                {...register('clientId')}
                                label="ID do Cliente"
                                fullWidth
                                disabled={user?.role === 'CLIENT'}
                                error={!!errors.clientId}
                                helperText={errors.clientId?.message}
                            />
                        </Grid>

                        {/* Origem */}
                        <Grid item xs={12} sm={6}>
                            <TextField
                                {...register('origin')}
                                label="Origem"
                                fullWidth
                                error={!!errors.origin}
                                helperText={errors.origin?.message}
                            />
                        </Grid>

                        {/* Destino */}
                        <Grid item xs={12} sm={6}>
                            <TextField
                                {...register('destination')}
                                label="Destino"
                                fullWidth
                                error={!!errors.destination}
                                helperText={errors.destination?.message}
                            />
                        </Grid>

                        {/* Peso */}
                        <Grid item xs={12} sm={3}>
                            <TextField
                                {...register('weight')}
                                label="Peso (kg)"
                                type="number"
                                fullWidth
                                error={!!errors.weight}
                                helperText={errors.weight?.message}
                            />
                        </Grid>

                        {/* Volume */}
                        <Grid item xs={12} sm={3}>
                            <TextField
                                {...register('volume')}
                                label="Volume (m³)"
                                type="number"
                                fullWidth
                                error={!!errors.volume}
                                helperText={errors.volume?.message}
                            />
                        </Grid>

                        {/* Tipo de produto */}
                        <Grid item xs={12} sm={6}>
                            <TextField
                                {...register('productType')}
                                label="Tipo de Produto"
                                fullWidth
                                error={!!errors.productType}
                                helperText={errors.productType?.message}
                            />
                        </Grid>

                        {/* Opções */}
                        <Grid item xs={12}>
                            <FormControlLabel
                                control={<Checkbox {...register('rush')} />}
                                label="Entrega Urgente (+25%)"
                            />

                            <FormControlLabel
                                control={<Checkbox {...register('fragile')} />}
                                label="Produto Frágil (+15%)"
                            />

                            <FormControlLabel
                                control={<Checkbox {...register('insurance')} />}
                                label="Seguro de Carga (+10%)"
                            />
                        </Grid>

                        {/* Submit Button */}
                        <Grid item xs={12}>
                            <Button
                                type="submit"
                                variant="contained"
                                color="primary"
                                disabled={loading}
                                sx={{ mt: 2 }}
                            >
                                {loading ? <CircularProgress size={24} /> : 'Calcular Frete'}
                            </Button>
                        </Grid>
                    </Grid>
                </Box>
            </CardContent>
        </Card>
    );
};

export default FreightForm;