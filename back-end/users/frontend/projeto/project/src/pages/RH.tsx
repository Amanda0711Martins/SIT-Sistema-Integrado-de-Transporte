import React from 'react';
import { UserCheck, Users, Calendar, DollarSign, Clock, Plus } from 'lucide-react';

export const RH: React.FC = () => {
  const stats = [
    { label: 'Total de Funcionários', value: '247', icon: Users, color: 'bg-blue-500' },
    { label: 'Presentes Hoje', value: '231', icon: UserCheck, color: 'bg-green-500' },
    { label: 'Folha de Pagamento', value: 'R$ 485K', icon: DollarSign, color: 'bg-purple-500' },
    { label: 'Horas Extras', value: '1,247h', icon: Clock, color: 'bg-orange-500' },
  ];

  const recentEmployees = [
    { name: 'João Silva', role: 'Motorista', department: 'Operacional', status: 'Ativo', joinDate: '2024-01-15' },
    { name: 'Maria Santos', role: 'Analista Financeiro', department: 'Financeiro', status: 'Ativo', joinDate: '2024-01-10' },
    { name: 'Pedro Costa', role: 'Mecânico', department: 'Manutenção', status: 'Ativo', joinDate: '2024-01-08' },
    { name: 'Ana Oliveira', role: 'Atendente', department: 'Comercial', status: 'Ativo', joinDate: '2024-01-05' },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Recursos Humanos</h1>
          <p className="text-gray-600 mt-2">Gerenciamento de funcionários, folha de pagamento e controle de ponto</p>
        </div>
        <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
          <Plus size={20} className="mr-2" />
          Novo Funcionário
        </button>
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
        {/* Recent Employees */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Funcionários Recentes</h2>
          <div className="space-y-4">
            {recentEmployees.map((employee, index) => (
              <div key={index} className="flex items-center justify-between p-4 hover:bg-gray-50 rounded-lg transition-colors">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                    <span className="text-blue-600 font-semibold">{employee.name.charAt(0)}</span>
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">{employee.name}</p>
                    <p className="text-sm text-gray-600">{employee.role} • {employee.department}</p>
                  </div>
                </div>
                <div className="text-right">
                  <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                    {employee.status}
                  </span>
                  <p className="text-xs text-gray-500 mt-1">{employee.joinDate}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Ações Rápidas</h2>
          <div className="space-y-3">
            <button className="w-full text-left p-4 hover:bg-gray-50 rounded-lg transition-colors border border-gray-200">
              <div className="flex items-center space-x-3">
                <Calendar className="text-blue-600" size={20} />
                <div>
                  <p className="font-medium text-gray-900">Registrar Ponto</p>
                  <p className="text-sm text-gray-600">Controle de entrada e saída</p>
                </div>
              </div>
            </button>
            <button className="w-full text-left p-4 hover:bg-gray-50 rounded-lg transition-colors border border-gray-200">
              <div className="flex items-center space-x-3">
                <DollarSign className="text-green-600" size={20} />
                <div>
                  <p className="font-medium text-gray-900">Folha de Pagamento</p>
                  <p className="text-sm text-gray-600">Gerar relatórios de pagamento</p>
                </div>
              </div>
            </button>
            <button className="w-full text-left p-4 hover:bg-gray-50 rounded-lg transition-colors border border-gray-200">
              <div className="flex items-center space-x-3">
                <Users className="text-purple-600" size={20} />
                <div>
                  <p className="font-medium text-gray-900">Relatórios</p>
                  <p className="text-sm text-gray-600">Visualizar relatórios de RH</p>
                </div>
              </div>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};