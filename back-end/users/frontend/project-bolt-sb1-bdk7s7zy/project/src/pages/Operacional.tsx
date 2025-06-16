import React from 'react';
import { Truck, Package, MapPin, Fuel, Plus, Calendar } from 'lucide-react';

export const Operacional: React.FC = () => {
  const stats = [
    { label: 'Veículos Ativos', value: '84', icon: Truck, color: 'bg-green-500' },
    { label: 'Coletas Pendentes', value: '23', icon: Package, color: 'bg-orange-500' },
    { label: 'Rotas Ativas', value: '156', icon: MapPin, color: 'bg-blue-500' },
    { label: 'Consumo Combustível', value: '2.8K L', icon: Fuel, color: 'bg-red-500' },
  ];

  const vehicles = [
    { plate: 'ABC-1234', driver: 'João Silva', status: 'Em Rota', location: 'São Paulo - Campinas', fuel: '85%' },
    { plate: 'DEF-5678', driver: 'Carlos Santos', status: 'Disponível', location: 'Base Principal', fuel: '92%' },
    { plate: 'GHI-9012', driver: 'Pedro Costa', status: 'Manutenção', location: 'Oficina', fuel: '45%' },
    { plate: 'JKL-3456', driver: 'Ana Oliveira', status: 'Em Rota', location: 'Rio de Janeiro - Belo Horizonte', fuel: '67%' },
  ];

  const pendingPickups = [
    { id: '#001', client: 'Empresa XYZ Ltda', origin: 'São Paulo', destination: 'Rio de Janeiro', date: '2024-01-20' },
    { id: '#002', client: 'Comércio ABC', origin: 'Belo Horizonte', destination: 'Brasília', date: '2024-01-21' },
    { id: '#003', client: 'Indústria 123', origin: 'Campinas', destination: 'Salvador', date: '2024-01-22' },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Operacional</h1>
          <p className="text-gray-600 mt-2">Gestão de cotações, pedidos de coleta e frota de veículos</p>
        </div>
        <div className="flex space-x-3">
          <button className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors flex items-center">
            <Plus size={20} className="mr-2" />
            Nova Coleta
          </button>
          <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
            <Calendar size={20} className="mr-2" />
            Agendar Rota
          </button>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.label} className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.label}</p>
                  <p className="text-3xl font-bold text-gray-900 mt-2">{stat.value}</p>
                </div>
                <div className={`${stat.color} p-3 rounded-lg`}>
                  <Icon size={24} className="text-white" />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Fleet Status */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Status da Frota</h2>
          <div className="space-y-4">
            {vehicles.map((vehicle, index) => (
              <div key={index} className="flex items-center justify-between p-4 hover:bg-gray-50 rounded-lg transition-colors border border-gray-100">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                    <Truck size={20} className="text-green-600" />
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">{vehicle.plate}</p>
                    <p className="text-sm text-gray-600">{vehicle.driver}</p>
                  </div>
                </div>
                <div className="text-right">
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    vehicle.status === 'Em Rota' ? 'bg-blue-100 text-blue-800' :
                    vehicle.status === 'Disponível' ? 'bg-green-100 text-green-800' :
                    'bg-red-100 text-red-800'
                  }`}>
                    {vehicle.status}
                  </span>
                  <p className="text-xs text-gray-500 mt-1">{vehicle.location}</p>
                  <p className="text-xs text-gray-500">Combustível: {vehicle.fuel}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Pending Pickups */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Coletas Pendentes</h2>
          <div className="space-y-4">
            {pendingPickups.map((pickup, index) => (
              <div key={index} className="p-4 hover:bg-gray-50 rounded-lg transition-colors border border-gray-100">
                <div className="flex items-center justify-between mb-2">
                  <span className="font-medium text-gray-900">{pickup.id}</span>
                  <span className="text-sm text-orange-600 font-medium">{pickup.date}</span>
                </div>
                <p className="text-sm text-gray-600 mb-1">{pickup.client}</p>
                <div className="flex items-center text-xs text-gray-500">
                  <MapPin size={12} className="mr-1" />
                  {pickup.origin} → {pickup.destination}
                </div>
              </div>
            ))}
          </div>
          <div className="mt-4 pt-4 border-t border-gray-200">
            <button className="text-sm text-blue-600 hover:text-blue-700 font-medium">
              Ver todas as coletas →
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};