import React from 'react';
import { Users, UserPlus, Mail, Phone, Plus, Search } from 'lucide-react';

export const Clientes: React.FC = () => {
  const stats = [
    { label: 'Total de Clientes', value: '1,234', icon: Users, color: 'bg-purple-500' },
    { label: 'Novos este Mês', value: '47', icon: UserPlus, color: 'bg-green-500' },
    { label: 'Clientes Ativos', value: '1,187', icon: Users, color: 'bg-blue-500' },
    { label: 'Taxa de Retenção', value: '94.2%', icon: Users, color: 'bg-orange-500' },
  ];

  const recentClients = [
    { 
      name: 'Empresa XYZ Ltda', 
      contact: 'João Silva', 
      email: 'joao@empresaxyz.com',
      phone: '(11) 99999-9999',
      city: 'São Paulo',
      status: 'Ativo',
      joinDate: '2024-01-15'
    },
    { 
      name: 'Comércio ABC', 
      contact: 'Maria Santos', 
      email: 'maria@comercioabc.com',
      phone: '(21) 88888-8888',
      city: 'Rio de Janeiro',
      status: 'Ativo',
      joinDate: '2024-01-10'
    },
    { 
      name: 'Indústria 123', 
      contact: 'Pedro Costa', 
      email: 'pedro@industria123.com',
      phone: '(31) 77777-7777',
      city: 'Belo Horizonte',
      status: 'Ativo',
      joinDate: '2024-01-08'
    },
    { 
      name: 'Distribuidora DEF', 
      contact: 'Ana Oliveira', 
      email: 'ana@distribuidoradef.com',
      phone: '(41) 66666-6666',
      city: 'Curitiba',
      status: 'Pendente',
      joinDate: '2024-01-05'
    },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-4 sm:space-y-0">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Clientes</h1>
          <p className="text-gray-600 mt-2">Cadastro, manutenção e consulta de informações de clientes</p>
        </div>
        <div className="flex space-x-3">
          <div className="relative">
            <Search size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar cliente..."
              className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <button className="bg-purple-600 text-white px-4 py-2 rounded-lg hover:bg-purple-700 transition-colors flex items-center">
            <Plus size={20} className="mr-2" />
            Novo Cliente
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

      {/* Clients Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-xl font-semibold text-gray-900">Clientes Recentes</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Cliente
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Contato
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Localização
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Data Cadastro
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {recentClients.map((client, index) => (
                <tr key={index} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="w-10 h-10 bg-purple-100 rounded-full flex items-center justify-center">
                        <span className="text-purple-600 font-semibold">{client.name.charAt(0)}</span>
                      </div>
                      <div className="ml-3">
                        <p className="font-medium text-gray-900">{client.name}</p>
                        <p className="text-sm text-gray-600">{client.contact}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="space-y-1">
                      <div className="flex items-center text-sm text-gray-600">
                        <Mail size={14} className="mr-2" />
                        {client.email}
                      </div>
                      <div className="flex items-center text-sm text-gray-600">
                        <Phone size={14} className="mr-2" />
                        {client.phone}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {client.city}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      client.status === 'Ativo' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {client.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {client.joinDate}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="px-6 py-4 border-t border-gray-200">
          <button className="text-sm text-purple-600 hover:text-purple-700 font-medium">
            Ver todos os clientes →
          </button>
        </div>
      </div>
    </div>
  );
};