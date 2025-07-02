import React from 'react';
import { DollarSign, CreditCard, FileText, TrendingUp, Plus, Download } from 'lucide-react';

export const Financeiro: React.FC = () => {
  const stats = [
    { label: 'Faturamento Mensal', value: 'R$ 2.4M', icon: DollarSign, color: 'bg-green-500' },
    { label: 'Contas a Receber', value: 'R$ 485K', icon: CreditCard, color: 'bg-blue-500' },
    { label: 'Contas a Pagar', value: 'R$ 127K', icon: FileText, color: 'bg-red-500' },
    { label: 'Lucro Líquido', value: 'R$ 320K', icon: TrendingUp, color: 'bg-purple-500' },
  ];

  const pendingInvoices = [
    { id: 'NF-001', client: 'Empresa XYZ Ltda', amount: 'R$ 15.000', dueDate: '2024-01-25', status: 'Pendente' },
    { id: 'NF-002', client: 'Comércio ABC', amount: 'R$ 8.500', dueDate: '2024-01-22', status: 'Vencida' },
    { id: 'NF-003', client: 'Indústria 123', amount: 'R$ 22.000', dueDate: '2024-01-28', status: 'Pendente' },
    { id: 'NF-004', client: 'Distribuidora DEF', amount: 'R$ 12.300', dueDate: '2024-01-30', status: 'Pendente' },
  ];

  const recentTransactions = [
    { type: 'Receita', description: 'Pagamento NF-045', amount: '+R$ 18.500', date: '2024-01-18' },
    { type: 'Despesa', description: 'Combustível - Frota', amount: '-R$ 4.200', date: '2024-01-18' },
    { type: 'Receita', description: 'Pagamento NF-046', amount: '+R$ 25.000', date: '2024-01-17' },
    { type: 'Despesa', description: 'Manutenção Veículo', amount: '-R$ 1.800', date: '2024-01-17' },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-4 sm:space-y-0">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Financeiro</h1>
          <p className="text-gray-600 mt-2">Controle de contas a pagar, emissão de cobranças e gestão de notas fiscais</p>
        </div>
        <div className="flex space-x-3">
          <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
            <Download size={20} className="mr-2" />
            Relatório
          </button>
          <button className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors flex items-center">
            <Plus size={20} className="mr-2" />
            Nova Cobrança
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
        {/* Pending Invoices */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Cobranças Pendentes</h2>
          <div className="space-y-4">
            {pendingInvoices.map((invoice, index) => (
              <div key={index} className="p-4 hover:bg-gray-50 rounded-lg transition-colors border border-gray-100">
                <div className="flex items-center justify-between mb-2">
                  <span className="font-medium text-gray-900">{invoice.id}</span>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    invoice.status === 'Pendente' ? 'bg-yellow-100 text-yellow-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {invoice.status}
                  </span>
                </div>
                <p className="text-sm text-gray-600 mb-1">{invoice.client}</p>
                <div className="flex items-center justify-between">
                  <span className="text-lg font-semibold text-green-600">{invoice.amount}</span>
                  <span className="text-sm text-gray-500">Vence em {invoice.dueDate}</span>
                </div>
              </div>
            ))}
          </div>
          <div className="mt-4 pt-4 border-t border-gray-200">
            <button className="text-sm text-blue-600 hover:text-blue-700 font-medium">
              Ver todas as cobranças →
            </button>
          </div>
        </div>

        {/* Recent Transactions */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Transações Recentes</h2>
          <div className="space-y-4">
            {recentTransactions.map((transaction, index) => (
              <div key={index} className="flex items-center justify-between p-4 hover:bg-gray-50 rounded-lg transition-colors">
                <div className="flex items-center space-x-3">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                    transaction.type === 'Receita' ? 'bg-green-100' : 'bg-red-100'
                  }`}>
                    {transaction.type === 'Receita' ? (
                      <TrendingUp size={16} className="text-green-600" />
                    ) : (
                      <DollarSign size={16} className="text-red-600" />
                    )}
                  </div>
                  <div>
                    <p className="font-medium text-gray-900 text-sm">{transaction.description}</p>
                    <p className="text-xs text-gray-500">{transaction.date}</p>
                  </div>
                </div>
                <span className={`font-semibold ${
                  transaction.type === 'Receita' ? 'text-green-600' : 'text-red-600'
                }`}>
                  {transaction.amount}
                </span>
              </div>
            ))}
          </div>
          <div className="mt-4 pt-4 border-t border-gray-200">
            <button className="text-sm text-blue-600 hover:text-blue-700 font-medium">
              Ver todas as transações →
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};