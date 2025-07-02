import React from 'react';
import { Link } from 'react-router-dom';
import {
  Users,
  Truck,
  UserCheck,
  DollarSign,
  Settings,
  TrendingUp,
  AlertTriangle,
  Package,
  Clock
} from 'lucide-react';

export const Dashboard: React.FC = () => {
  const stats = [
    { label: 'Funcionários Ativos', value: '247', icon: UserCheck, color: 'bg-blue-500', change: '+5.2%' },
    { label: 'Veículos em Operação', value: '84', icon: Truck, color: 'bg-green-500', change: '+2.1%' },
    { label: 'Clientes Ativos', value: '1,234', icon: Users, color: 'bg-purple-500', change: '+8.7%' },
    { label: 'Faturamento Mensal', value: 'R$ 2.4M', icon: DollarSign, color: 'bg-orange-500', change: '+12.3%' },
  ];

  const microservices = [
    {
      title: 'Recursos Humanos',
      description: 'Gerenciamento de funcionários, folha de pagamento e controle de ponto',
      icon: UserCheck,
      path: '/rh',
      color: 'from-blue-500 to-blue-600',
      stats: '247 funcionários'
    },
    {
      title: 'Operacional',
      description: 'Gestão de cotações, pedidos de coleta e frota de veículos',
      icon: Truck,
      path: '/operacional',
      color: 'from-green-500 to-green-600',
      stats: '84 veículos'
    },
    {
      title: 'Clientes',
      description: 'Cadastro, manutenção e consulta de informações de clientes',
      icon: Users,
      path: '/clientes',
      color: 'from-purple-500 to-purple-600',
      stats: '1,234 clientes'
    },
    {
      title: 'Financeiro',
      description: 'Controle de contas a pagar, emissão de cobranças e gestão de notas fiscais',
      icon: DollarSign,
      path: '/financeiro',
      color: 'from-orange-500 to-orange-600',
      stats: 'R$ 2.4M faturamento'
    },
    {
      title: 'Administração',
      description: 'Administração do sistema e dashboards gerenciais',
      icon: Settings,
      path: '/admin',
      color: 'from-gray-500 to-gray-600',
      stats: '5 usuários admin'
    },
  ];

  const alerts = [
    { type: 'warning', message: '3 veículos precisam de manutenção', time: '2h atrás' },
    { type: 'info', message: 'Nova coleta agendada para amanhã', time: '4h atrás' },
    { type: 'success', message: 'Pagamento de R$ 45.000 recebido', time: '6h atrás' },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600 mt-2">Visão geral das operações da transportadora</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.label} className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.label}</p>
                  <p className="text-3xl font-bold text-gray-900 mt-2">{stat.value}</p>
                  <p className="text-sm text-green-600 font-medium mt-1 flex items-center">
                    <TrendingUp size={16} className="mr-1" />
                    {stat.change}
                  </p>
                </div>
                <div className={`${stat.color} p-3 rounded-lg`}>
                  <Icon size={24} className="text-white" />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Microservices Grid */}
        <div className="lg:col-span-2">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Módulos do Sistema</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {microservices.map((service) => {
              const Icon = service.icon;
              return (
                <Link
                  key={service.path}
                  to={service.path}
                  className="group bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-lg transition-all duration-200 transform hover:scale-105"
                >
                  <div className={`w-12 h-12 bg-gradient-to-r ${service.color} rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform`}>
                    <Icon size={24} className="text-white" />
                  </div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">{service.title}</h3>
                  <p className="text-gray-600 text-sm mb-3 line-clamp-2">{service.description}</p>
                  <p className="text-xs font-medium text-gray-500">{service.stats}</p>
                </Link>
              );
            })}
          </div>
        </div>

        {/* Alerts Panel */}
        <div>
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Alertas Recentes</h2>
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div className="space-y-4">
              {alerts.map((alert, index) => (
                <div key={index} className="flex items-start space-x-3 p-3 rounded-lg hover:bg-gray-50 transition-colors">
                  <div className={`p-1 rounded-full ${
                    alert.type === 'warning' ? 'bg-yellow-100' :
                    alert.type === 'info' ? 'bg-blue-100' :
                    'bg-green-100'
                  }`}>
                    {alert.type === 'warning' ? (
                      <AlertTriangle size={16} className="text-yellow-600" />
                    ) : alert.type === 'info' ? (
                      <Package size={16} className="text-blue-600" />
                    ) : (
                      <DollarSign size={16} className="text-green-600" />
                    )}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-gray-900">{alert.message}</p>
                    <p className="text-xs text-gray-500 flex items-center mt-1">
                      <Clock size={12} className="mr-1" />
                      {alert.time}
                    </p>
                  </div>
                </div>
              ))}
            </div>
            <div className="mt-4 pt-4 border-t border-gray-200">
              <button className="text-sm text-blue-600 hover:text-blue-700 font-medium">
                Ver todos os alertas →
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};