import React from 'react';
import { Settings, Users, Shield, Database, Activity, Plus, AlertTriangle } from 'lucide-react';

export const Admin: React.FC = () => {
  const stats = [
    { label: 'Usuários do Sistema', value: '45', icon: Users, color: 'bg-blue-500' },
    { label: 'Microserviços Ativos', value: '5', icon: Database, color: 'bg-green-500' },
    { label: 'Uptime do Sistema', value: '99.8%', icon: Activity, color: 'bg-purple-500' },
    { label: 'Logs de Segurança', value: '127', icon: Shield, color: 'bg-orange-500' },
  ];

  const systemUsers = [
    { name: 'Admin Sistema', email: 'admin@sistema.com', role: 'Super Admin', status: 'Ativo', lastLogin: '2024-01-18 14:30' },
    { name: 'João Silva', email: 'joao@transportadora.com', role: 'Admin RH', status: 'Ativo', lastLogin: '2024-01-18 09:15' },
    { name: 'Maria Santos', email: 'maria@transportadora.com', role: 'Admin Financeiro', status: 'Ativo', lastLogin: '2024-01-17 16:45' },
    { name: 'Pedro Costa', email: 'pedro@transportadora.com', role: 'Admin Operacional', status: 'Inativo', lastLogin: '2024-01-15 11:20' },
  ];

  const microservices = [
    { name: 'Recursos Humanos', status: 'Online', uptime: '99.9%', version: 'v2.1.3', lastUpdate: '2024-01-15' },
    { name: 'Operacional', status: 'Online', uptime: '99.7%', version: 'v1.8.2', lastUpdate: '2024-01-12' },
    { name: 'Clientes', status: 'Online', uptime: '99.8%', version: 'v2.0.1', lastUpdate: '2024-01-18' },
    { name: 'Financeiro', status: 'Manutenção', uptime: '98.5%', version: 'v1.9.4', lastUpdate: '2024-01-10' },
    { name: 'Usuários', status: 'Online', uptime: '100%', version: 'v2.2.0', lastUpdate: '2024-01-17' },
  ];

  const systemLogs = [
    { type: 'Info', message: 'Sistema iniciado com sucesso', time: '2024-01-18 14:35' },
    { type: 'Warning', message: 'Alto uso de CPU no microserviço Financeiro', time: '2024-01-18 14:20' },
    { type: 'Success', message: 'Backup automático concluído', time: '2024-01-18 14:00' },
    { type: 'Error', message: 'Falha na conexão com banco auxiliar', time: '2024-01-18 13:45' },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-4 sm:space-y-0">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Administração</h1>
          <p className="text-gray-600 mt-2">Administração do sistema e dashboards gerenciais</p>
        </div>
        <div className="flex space-x-3">
          <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
            <Settings size={20} className="mr-2" />
            Configurações
          </button>
          <button className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors flex items-center">
            <Plus size={20} className="mr-2" />
            Novo Usuário
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
        {/* System Users */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Usuários do Sistema</h2>
          <div className="space-y-4">
            {systemUsers.map((user, index) => (
              <div key={index} className="flex items-center justify-between p-4 hover:bg-gray-50 rounded-lg transition-colors border border-gray-100">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                    <span className="text-blue-600 font-semibold">{user.name.charAt(0)}</span>
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">{user.name}</p>
                    <p className="text-sm text-gray-600">{user.role} • {user.email}</p>
                    <p className="text-xs text-gray-500">Último login: {user.lastLogin}</p>
                  </div>
                </div>
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  user.status === 'Ativo' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                }`}>
                  {user.status}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Microservices Status */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Status dos Microserviços</h2>
          <div className="space-y-4">
            {microservices.map((service, index) => (
              <div key={index} className="p-4 hover:bg-gray-50 rounded-lg transition-colors border border-gray-100">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center space-x-2">
                    <span className="font-medium text-gray-900">{service.name}</span>
                    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                      service.status === 'Online' ? 'bg-green-100 text-green-800' : 
                      service.status === 'Manutenção' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {service.status}
                    </span>
                  </div>
                  <span className="text-sm text-gray-600">{service.version}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">Uptime: {service.uptime}</span>
                  <span className="text-xs text-gray-500">Atualizado: {service.lastUpdate}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* System Logs */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h2 className="text-xl font-semibold text-gray-900 mb-6">Logs do Sistema</h2>
        <div className="space-y-3">
          {systemLogs.map((log, index) => (
            <div key={index} className="flex items-start space-x-3 p-3 hover:bg-gray-50 rounded-lg transition-colors">
              <div className={`p-1 rounded-full ${
                log.type === 'Info' ? 'bg-blue-100' :
                log.type === 'Warning' ? 'bg-yellow-100' :
                log.type === 'Success' ? 'bg-green-100' :
                'bg-red-100'
              }`}>
                {log.type === 'Info' ? (
                  <Activity size={16} className="text-blue-600" />
                ) : log.type === 'Warning' ? (
                  <AlertTriangle size={16} className="text-yellow-600" />
                ) : log.type === 'Success' ? (
                  <Settings size={16} className="text-green-600" />
                ) : (
                  <AlertTriangle size={16} className="text-red-600" />
                )}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <p className="text-sm text-gray-900">{log.message}</p>
                  <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                    log.type === 'Info' ? 'bg-blue-100 text-blue-800' :
                    log.type === 'Warning' ? 'bg-yellow-100 text-yellow-800' :
                    log.type === 'Success' ? 'bg-green-100 text-green-800' :
                    'bg-red-100 text-red-800'
                  }`}>
                    {log.type}
                  </span>
                </div>
                <p className="text-xs text-gray-500 mt-1">{log.time}</p>
              </div>
            </div>
          ))}
        </div>
        <div className="mt-4 pt-4 border-t border-gray-200">
          <button className="text-sm text-blue-600 hover:text-blue-700 font-medium">
            Ver todos os logs →
          </button>
        </div>
      </div>
    </div>
  );
};