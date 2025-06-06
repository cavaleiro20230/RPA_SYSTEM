"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { FileText, Users, Building, Calculator, DollarSign, Plus, Edit, Eye, Download, CheckCircle } from "lucide-react"

interface Prestador {
  id: number
  nome: string
  cpf: string
  email: string
  telefone: string
  isMei: boolean
  status: string
}

interface Tomador {
  id: number
  razaoSocial: string
  cnpjCpf: string
  email: string
  regimeTributario: string
  status: string
}

interface Rpa {
  id: number
  numero: string
  prestador: Prestador
  tomador: Tomador
  dataEmissao: string
  descricaoServico: string
  valorBruto: number
  valorInss: number
  valorIrrf: number
  valorIss: number
  totalDescontos: number
  valorLiquido: number
  status: string
}

export default function RpaFiscalDashboard() {
  const [rpas, setRpas] = useState<Rpa[]>([])
  const [prestadores, setPrestadores] = useState<Prestador[]>([])
  const [tomadores, setTomadores] = useState<Tomador[]>([])
  const [isCreateRpaDialogOpen, setIsCreateRpaDialogOpen] = useState(false)
  const [isCreatePrestadorDialogOpen, setIsCreatePrestadorDialogOpen] = useState(false)
  const [newRpa, setNewRpa] = useState({
    prestadorId: "",
    tomadorId: "",
    descricaoServico: "",
    valorBruto: "",
    dataVencimento: "",
    observacoes: "",
  })
  const [newPrestador, setNewPrestador] = useState({
    nome: "",
    cpf: "",
    email: "",
    telefone: "",
    endereco: "",
    isMei: false,
    atividadePrincipal: "",
  })

  // Dados simulados para demonstração
  useEffect(() => {
    const mockPrestadores: Prestador[] = [
      {
        id: 1,
        nome: "João Silva Santos",
        cpf: "123.456.789-01",
        email: "joao@email.com",
        telefone: "(11) 99999-1234",
        isMei: false,
        status: "ATIVO",
      },
      {
        id: 2,
        nome: "Maria Oliveira Costa",
        cpf: "987.654.321-02",
        email: "maria@email.com",
        telefone: "(11) 88888-5678",
        isMei: true,
        status: "ATIVO",
      },
    ]

    const mockTomadores: Tomador[] = [
      {
        id: 1,
        razaoSocial: "Tech Solutions Ltda",
        cnpjCpf: "12.345.678/0001-90",
        email: "contato@techsol.com.br",
        regimeTributario: "LUCRO_PRESUMIDO",
        status: "ATIVO",
      },
      {
        id: 2,
        razaoSocial: "Consultoria Empresarial S/A",
        cnpjCpf: "98.765.432/0001-10",
        email: "financeiro@consultemp.com.br",
        regimeTributario: "LUCRO_REAL",
        status: "ATIVO",
      },
    ]

    const mockRpas: Rpa[] = [
      {
        id: 1,
        numero: "RPA-2024-01-0001",
        prestador: mockPrestadores[0],
        tomador: mockTomadores[0],
        dataEmissao: "2024-01-15",
        descricaoServico: "Desenvolvimento de sistema web para gestão de estoque",
        valorBruto: 5000.0,
        valorInss: 450.0,
        valorIrrf: 375.0,
        valorIss: 100.0,
        totalDescontos: 925.0,
        valorLiquido: 4075.0,
        status: "EMITIDO",
      },
      {
        id: 2,
        numero: "RPA-2024-01-0002",
        prestador: mockPrestadores[1],
        tomador: mockTomadores[1],
        dataEmissao: "2024-01-20",
        descricaoServico: "Consultoria em processos empresariais - Janeiro/2024",
        valorBruto: 3000.0,
        valorInss: 0.0, // MEI isento
        valorIrrf: 225.0,
        valorIss: 60.0,
        totalDescontos: 285.0,
        valorLiquido: 2715.0,
        status: "PAGO",
      },
    ]

    setPrestadores(mockPrestadores)
    setTomadores(mockTomadores)
    setRpas(mockRpas)
  }, [])

  const handleCreateRpa = () => {
    const prestador = prestadores.find((p) => p.id === Number.parseInt(newRpa.prestadorId))
    const tomador = tomadores.find((t) => t.id === Number.parseInt(newRpa.tomadorId))

    if (!prestador || !tomador) return

    // Simular cálculos fiscais
    const valorBruto = Number.parseFloat(newRpa.valorBruto)
    const valorInss = prestador.isMei ? 0 : valorBruto * 0.09 // 9% INSS
    const valorIrrf = (valorBruto - valorInss) * 0.075 // 7.5% IRRF
    const valorIss = valorBruto * 0.02 // 2% ISS
    const totalDescontos = valorInss + valorIrrf + valorIss
    const valorLiquido = valorBruto - totalDescontos

    const novoRpa: Rpa = {
      id: Date.now(),
      numero: `RPA-2024-01-${String(rpas.length + 1).padStart(4, "0")}`,
      prestador,
      tomador,
      dataEmissao: new Date().toISOString().split("T")[0],
      descricaoServico: newRpa.descricaoServico,
      valorBruto,
      valorInss,
      valorIrrf,
      valorIss,
      totalDescontos,
      valorLiquido,
      status: "EMITIDO",
    }

    setRpas([novoRpa, ...rpas])
    setNewRpa({
      prestadorId: "",
      tomadorId: "",
      descricaoServico: "",
      valorBruto: "",
      dataVencimento: "",
      observacoes: "",
    })
    setIsCreateRpaDialogOpen(false)
  }

  const handleCreatePrestador = () => {
    const novoPrestador: Prestador = {
      id: Date.now(),
      ...newPrestador,
      status: "ATIVO",
    }
    setPrestadores([...prestadores, novoPrestador])
    setNewPrestador({
      nome: "",
      cpf: "",
      email: "",
      telefone: "",
      endereco: "",
      isMei: false,
      atividadePrincipal: "",
    })
    setIsCreatePrestadorDialogOpen(false)
  }

  const getStatusBadge = (status: string) => {
    const variants: Record<string, "default" | "secondary" | "destructive" | "outline"> = {
      EMITIDO: "secondary",
      PAGO: "default",
      CANCELADO: "destructive",
      VENCIDO: "outline",
    }
    return <Badge variant={variants[status] || "outline"}>{status}</Badge>
  }

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(value)
  }

  const totalRpasEmitidos = rpas.length
  const totalValorBruto = rpas.reduce((sum, rpa) => sum + rpa.valorBruto, 0)
  const totalImpostosRetidos = rpas.reduce((sum, rpa) => sum + rpa.totalDescontos, 0)
  const totalValorLiquido = rpas.reduce((sum, rpa) => sum + rpa.valorLiquido, 0)

  return (
    <div className="container mx-auto p-6 space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Sistema RPA Fiscal</h1>
          <p className="text-muted-foreground">Gestão de Recibos de Pagamento Autônomo</p>
        </div>
        <div className="flex space-x-2">
          <Dialog open={isCreatePrestadorDialogOpen} onOpenChange={setIsCreatePrestadorDialogOpen}>
            <DialogTrigger asChild>
              <Button variant="outline">
                <Users className="w-4 h-4 mr-2" />
                Novo Prestador
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Cadastrar Prestador de Serviço</DialogTitle>
                <DialogDescription>Cadastre um novo prestador autônomo</DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="nome" className="text-right">
                    Nome
                  </Label>
                  <Input
                    id="nome"
                    value={newPrestador.nome}
                    onChange={(e) => setNewPrestador({ ...newPrestador, nome: e.target.value })}
                    className="col-span-3"
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="cpf" className="text-right">
                    CPF
                  </Label>
                  <Input
                    id="cpf"
                    value={newPrestador.cpf}
                    onChange={(e) => setNewPrestador({ ...newPrestador, cpf: e.target.value })}
                    className="col-span-3"
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="email" className="text-right">
                    Email
                  </Label>
                  <Input
                    id="email"
                    type="email"
                    value={newPrestador.email}
                    onChange={(e) => setNewPrestador({ ...newPrestador, email: e.target.value })}
                    className="col-span-3"
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="telefone" className="text-right">
                    Telefone
                  </Label>
                  <Input
                    id="telefone"
                    value={newPrestador.telefone}
                    onChange={(e) => setNewPrestador({ ...newPrestador, telefone: e.target.value })}
                    className="col-span-3"
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="atividade" className="text-right">
                    Atividade
                  </Label>
                  <Input
                    id="atividade"
                    value={newPrestador.atividadePrincipal}
                    onChange={(e) => setNewPrestador({ ...newPrestador, atividadePrincipal: e.target.value })}
                    className="col-span-3"
                  />
                </div>
              </div>
              <DialogFooter>
                <Button onClick={handleCreatePrestador}>Cadastrar</Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>

          <Dialog open={isCreateRpaDialogOpen} onOpenChange={setIsCreateRpaDialogOpen}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="w-4 h-4 mr-2" />
                Novo RPA
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl">
              <DialogHeader>
                <DialogTitle>Emitir Novo RPA</DialogTitle>
                <DialogDescription>Crie um novo Recibo de Pagamento Autônomo</DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="prestador" className="text-right">
                    Prestador
                  </Label>
                  <Select
                    value={newRpa.prestadorId}
                    onValueChange={(value) => setNewRpa({ ...newRpa, prestadorId: value })}
                  >
                    <SelectTrigger className="col-span-3">
                      <SelectValue placeholder="Selecione o prestador" />
                    </SelectTrigger>
                    <SelectContent>
                      {prestadores.map((prestador) => (
                        <SelectItem key={prestador.id} value={prestador.id.toString()}>
                          {prestador.nome} - {prestador.cpf}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="tomador" className="text-right">
                    Tomador
                  </Label>
                  <Select
                    value={newRpa.tomadorId}
                    onValueChange={(value) => setNewRpa({ ...newRpa, tomadorId: value })}
                  >
                    <SelectTrigger className="col-span-3">
                      <SelectValue placeholder="Selecione o tomador" />
                    </SelectTrigger>
                    <SelectContent>
                      {tomadores.map((tomador) => (
                        <SelectItem key={tomador.id} value={tomador.id.toString()}>
                          {tomador.razaoSocial} - {tomador.cnpjCpf}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="valorBruto" className="text-right">
                    Valor Bruto
                  </Label>
                  <Input
                    id="valorBruto"
                    type="number"
                    step="0.01"
                    value={newRpa.valorBruto}
                    onChange={(e) => setNewRpa({ ...newRpa, valorBruto: e.target.value })}
                    className="col-span-3"
                  />
                </div>
                <div className="grid grid-cols-4 items-start gap-4">
                  <Label htmlFor="descricaoServico" className="text-right">
                    Descrição do Serviço
                  </Label>
                  <Textarea
                    id="descricaoServico"
                    value={newRpa.descricaoServico}
                    onChange={(e) => setNewRpa({ ...newRpa, descricaoServico: e.target.value })}
                    className="col-span-3"
                    rows={3}
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="dataVencimento" className="text-right">
                    Data Vencimento
                  </Label>
                  <Input
                    id="dataVencimento"
                    type="date"
                    value={newRpa.dataVencimento}
                    onChange={(e) => setNewRpa({ ...newRpa, dataVencimento: e.target.value })}
                    className="col-span-3"
                  />
                </div>
                <div className="grid grid-cols-4 items-start gap-4">
                  <Label htmlFor="observacoes" className="text-right">
                    Observações
                  </Label>
                  <Textarea
                    id="observacoes"
                    value={newRpa.observacoes}
                    onChange={(e) => setNewRpa({ ...newRpa, observacoes: e.target.value })}
                    className="col-span-3"
                    rows={2}
                  />
                </div>
              </div>
              <DialogFooter>
                <Button onClick={handleCreateRpa}>Emitir RPA</Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Cards de Estatísticas */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">RPAs Emitidos</CardTitle>
            <FileText className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{totalRpasEmitidos}</div>
            <p className="text-xs text-muted-foreground">{rpas.filter((r) => r.status === "PAGO").length} pagos</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Valor Bruto Total</CardTitle>
            <DollarSign className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatCurrency(totalValorBruto)}</div>
            <p className="text-xs text-muted-foreground">Valor total dos serviços</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Impostos Retidos</CardTitle>
            <Calculator className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatCurrency(totalImpostosRetidos)}</div>
            <p className="text-xs text-muted-foreground">INSS + IRRF + ISS</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Valor Líquido</CardTitle>
            <Building className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatCurrency(totalValorLiquido)}</div>
            <p className="text-xs text-muted-foreground">Valor a pagar aos prestadores</p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="rpas" className="space-y-4">
        <TabsList>
          <TabsTrigger value="rpas">RPAs</TabsTrigger>
          <TabsTrigger value="prestadores">Prestadores</TabsTrigger>
          <TabsTrigger value="tomadores">Tomadores</TabsTrigger>
          <TabsTrigger value="relatorios">Relatórios</TabsTrigger>
        </TabsList>

        <TabsContent value="rpas" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Recibos de Pagamento Autônomo</CardTitle>
              <CardDescription>Gerencie os RPAs emitidos</CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Número</TableHead>
                    <TableHead>Prestador</TableHead>
                    <TableHead>Tomador</TableHead>
                    <TableHead>Data Emissão</TableHead>
                    <TableHead>Valor Bruto</TableHead>
                    <TableHead>Impostos</TableHead>
                    <TableHead>Valor Líquido</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {rpas.map((rpa) => (
                    <TableRow key={rpa.id}>
                      <TableCell className="font-medium">{rpa.numero}</TableCell>
                      <TableCell>
                        <div>
                          <div className="font-medium">{rpa.prestador.nome}</div>
                          <div className="text-sm text-muted-foreground">{rpa.prestador.cpf}</div>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div>
                          <div className="font-medium">{rpa.tomador.razaoSocial}</div>
                          <div className="text-sm text-muted-foreground">{rpa.tomador.cnpjCpf}</div>
                        </div>
                      </TableCell>
                      <TableCell>{new Date(rpa.dataEmissao).toLocaleDateString("pt-BR")}</TableCell>
                      <TableCell>{formatCurrency(rpa.valorBruto)}</TableCell>
                      <TableCell>
                        <div className="text-sm">
                          <div>INSS: {formatCurrency(rpa.valorInss)}</div>
                          <div>IRRF: {formatCurrency(rpa.valorIrrf)}</div>
                          <div>ISS: {formatCurrency(rpa.valorIss)}</div>
                        </div>
                      </TableCell>
                      <TableCell className="font-medium">{formatCurrency(rpa.valorLiquido)}</TableCell>
                      <TableCell>{getStatusBadge(rpa.status)}</TableCell>
                      <TableCell>
                        <div className="flex space-x-2">
                          <Button size="sm" variant="outline">
                            <Eye className="w-4 h-4" />
                          </Button>
                          <Button size="sm" variant="outline">
                            <Download className="w-4 h-4" />
                          </Button>
                          {rpa.status === "EMITIDO" && (
                            <Button size="sm" variant="outline">
                              <CheckCircle className="w-4 h-4" />
                            </Button>
                          )}
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="prestadores" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Prestadores de Serviço</CardTitle>
              <CardDescription>Cadastro de prestadores autônomos</CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Nome</TableHead>
                    <TableHead>CPF</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Telefone</TableHead>
                    <TableHead>MEI</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {prestadores.map((prestador) => (
                    <TableRow key={prestador.id}>
                      <TableCell className="font-medium">{prestador.nome}</TableCell>
                      <TableCell>{prestador.cpf}</TableCell>
                      <TableCell>{prestador.email}</TableCell>
                      <TableCell>{prestador.telefone}</TableCell>
                      <TableCell>
                        {prestador.isMei ? (
                          <Badge variant="default">MEI</Badge>
                        ) : (
                          <Badge variant="outline">Não MEI</Badge>
                        )}
                      </TableCell>
                      <TableCell>{getStatusBadge(prestador.status)}</TableCell>
                      <TableCell>
                        <div className="flex space-x-2">
                          <Button size="sm" variant="outline">
                            <Edit className="w-4 h-4" />
                          </Button>
                          <Button size="sm" variant="outline">
                            <Eye className="w-4 h-4" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="tomadores" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Tomadores de Serviço</CardTitle>
              <CardDescription>Empresas e pessoas físicas contratantes</CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Razão Social</TableHead>
                    <TableHead>CNPJ/CPF</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Regime Tributário</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {tomadores.map((tomador) => (
                    <TableRow key={tomador.id}>
                      <TableCell className="font-medium">{tomador.razaoSocial}</TableCell>
                      <TableCell>{tomador.cnpjCpf}</TableCell>
                      <TableCell>{tomador.email}</TableCell>
                      <TableCell>
                        <Badge variant="outline">{tomador.regimeTributario}</Badge>
                      </TableCell>
                      <TableCell>{getStatusBadge(tomador.status)}</TableCell>
                      <TableCell>
                        <div className="flex space-x-2">
                          <Button size="sm" variant="outline">
                            <Edit className="w-4 h-4" />
                          </Button>
                          <Button size="sm" variant="outline">
                            <Eye className="w-4 h-4" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="relatorios" className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Card>
              <CardHeader>
                <CardTitle>Relatórios Fiscais</CardTitle>
                <CardDescription>Gere relatórios para obrigações acessórias</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <Button className="w-full" variant="outline">
                  <FileText className="w-4 h-4 mr-2" />
                  Relatório DIRF
                </Button>
                <Button className="w-full" variant="outline">
                  <FileText className="w-4 h-4 mr-2" />
                  Relatório EFD-Reinf
                </Button>
                <Button className="w-full" variant="outline">
                  <FileText className="w-4 h-4 mr-2" />
                  Relatório DCTFWeb
                </Button>
                <Button className="w-full" variant="outline">
                  <FileText className="w-4 h-4 mr-2" />
                  Guias de Recolhimento
                </Button>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Resumo Mensal</CardTitle>
                <CardDescription>Janeiro 2024</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex justify-between">
                  <span>Total de RPAs:</span>
                  <span className="font-medium">{totalRpasEmitidos}</span>
                </div>
                <div className="flex justify-between">
                  <span>Valor Bruto:</span>
                  <span className="font-medium">{formatCurrency(totalValorBruto)}</span>
                </div>
                <div className="flex justify-between">
                  <span>INSS Retido:</span>
                  <span className="font-medium">
                    {formatCurrency(rpas.reduce((sum, rpa) => sum + rpa.valorInss, 0))}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span>IRRF Retido:</span>
                  <span className="font-medium">
                    {formatCurrency(rpas.reduce((sum, rpa) => sum + rpa.valorIrrf, 0))}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span>ISS Retido:</span>
                  <span className="font-medium">
                    {formatCurrency(rpas.reduce((sum, rpa) => sum + rpa.valorIss, 0))}
                  </span>
                </div>
                <div className="flex justify-between border-t pt-2">
                  <span className="font-medium">Valor Líquido:</span>
                  <span className="font-bold">{formatCurrency(totalValorLiquido)}</span>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  )
}
