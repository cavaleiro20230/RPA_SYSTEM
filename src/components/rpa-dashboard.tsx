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
import { Play, Calendar, Code, Database, Activity, Plus, Edit, Eye } from "lucide-react"

interface RpaProcess {
  id: number
  name: string
  description: string
  scriptContent: string
  scriptType: string
  status: string
  createdAt: string
  cronExpression?: string
}

interface RpaExecution {
  id: number
  status: string
  startTime: string
  endTime?: string
  durationMs?: number
  resultMessage?: string
  errorMessage?: string
  triggeredBy: string
}

export default function RpaDashboard() {
  const [processes, setProcesses] = useState<RpaProcess[]>([])
  const [executions, setExecutions] = useState<RpaExecution[]>([])
  const [selectedProcess, setSelectedProcess] = useState<RpaProcess | null>(null)
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [isExecutionDialogOpen, setIsExecutionDialogOpen] = useState(false)
  const [newProcess, setNewProcess] = useState({
    name: "",
    description: "",
    scriptContent: "",
    scriptType: "JAVA",
    cronExpression: "",
  })

  // Simular dados para demonstração
  useEffect(() => {
    const mockProcesses: RpaProcess[] = [
      {
        id: 1,
        name: "Relatório Diário de Vendas",
        description: "Gera relatório diário de vendas e envia por email",
        scriptContent: `public class RelatorioVendas {
    public void executar() {
        System.out.println("Iniciando geração de relatório...");
        // Lógica do relatório aqui
        System.out.println("Relatório gerado com sucesso!");
    }
}`,
        scriptType: "JAVA",
        status: "ACTIVE",
        createdAt: "2024-01-15T08:00:00",
        cronExpression: "0 0 8 * * ?",
      },
      {
        id: 2,
        name: "Backup de Dados",
        description: "Realiza backup automático dos dados críticos",
        scriptContent: `public class BackupDados {
    public void executar() {
        System.out.println("Iniciando backup...");
        // Lógica de backup aqui
        System.out.println("Backup concluído!");
    }
}`,
        scriptType: "JAVA",
        status: "ACTIVE",
        createdAt: "2024-01-10T14:30:00",
        cronExpression: "0 0 2 * * ?",
      },
    ]

    const mockExecutions: RpaExecution[] = [
      {
        id: 1,
        status: "SUCCESS",
        startTime: "2024-01-20T08:00:00",
        endTime: "2024-01-20T08:02:30",
        durationMs: 150000,
        resultMessage: "Relatório gerado com sucesso!",
        triggeredBy: "SCHEDULED",
      },
      {
        id: 2,
        status: "RUNNING",
        startTime: "2024-01-20T14:00:00",
        triggeredBy: "MANUAL",
      },
      {
        id: 3,
        status: "FAILED",
        startTime: "2024-01-19T08:00:00",
        endTime: "2024-01-19T08:01:15",
        durationMs: 75000,
        errorMessage: "Erro de conexão com o banco de dados",
        triggeredBy: "SCHEDULED",
      },
    ]

    setProcesses(mockProcesses)
    setExecutions(mockExecutions)
  }, [])

  const handleCreateProcess = () => {
    const process: RpaProcess = {
      id: Date.now(),
      ...newProcess,
      status: "ACTIVE",
      createdAt: new Date().toISOString(),
    }
    setProcesses([...processes, process])
    setNewProcess({
      name: "",
      description: "",
      scriptContent: "",
      scriptType: "JAVA",
      cronExpression: "",
    })
    setIsCreateDialogOpen(false)
  }

  const handleExecuteProcess = (processId: number) => {
    const execution: RpaExecution = {
      id: Date.now(),
      status: "RUNNING",
      startTime: new Date().toISOString(),
      triggeredBy: "MANUAL",
    }
    setExecutions([execution, ...executions])
  }

  const getStatusBadge = (status: string) => {
    const variants: Record<string, "default" | "secondary" | "destructive" | "outline"> = {
      SUCCESS: "default",
      RUNNING: "secondary",
      FAILED: "destructive",
      CANCELLED: "outline",
    }
    return <Badge variant={variants[status] || "outline"}>{status}</Badge>
  }

  const formatDuration = (ms?: number) => {
    if (!ms) return "-"
    const seconds = Math.floor(ms / 1000)
    const minutes = Math.floor(seconds / 60)
    return `${minutes}m ${seconds % 60}s`
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Sistema RPA</h1>
          <p className="text-muted-foreground">Automação de Processos Robóticos</p>
        </div>
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="w-4 h-4 mr-2" />
              Novo Processo
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>Criar Novo Processo RPA</DialogTitle>
              <DialogDescription>Configure um novo processo de automação</DialogDescription>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="name" className="text-right">
                  Nome
                </Label>
                <Input
                  id="name"
                  value={newProcess.name}
                  onChange={(e) => setNewProcess({ ...newProcess, name: e.target.value })}
                  className="col-span-3"
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="description" className="text-right">
                  Descrição
                </Label>
                <Textarea
                  id="description"
                  value={newProcess.description}
                  onChange={(e) => setNewProcess({ ...newProcess, description: e.target.value })}
                  className="col-span-3"
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="scriptType" className="text-right">
                  Tipo
                </Label>
                <Select
                  value={newProcess.scriptType}
                  onValueChange={(value) => setNewProcess({ ...newProcess, scriptType: value })}
                >
                  <SelectTrigger className="col-span-3">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="JAVA">Java</SelectItem>
                    <SelectItem value="PYTHON">Python</SelectItem>
                    <SelectItem value="SELENIUM">Selenium</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="cronExpression" className="text-right">
                  Agendamento
                </Label>
                <Input
                  id="cronExpression"
                  placeholder="0 0 8 * * ? (Todo dia às 8h)"
                  value={newProcess.cronExpression}
                  onChange={(e) => setNewProcess({ ...newProcess, cronExpression: e.target.value })}
                  className="col-span-3"
                />
              </div>
              <div className="grid grid-cols-4 items-start gap-4">
                <Label htmlFor="scriptContent" className="text-right">
                  Script
                </Label>
                <Textarea
                  id="scriptContent"
                  placeholder="Código do processo..."
                  value={newProcess.scriptContent}
                  onChange={(e) => setNewProcess({ ...newProcess, scriptContent: e.target.value })}
                  className="col-span-3 min-h-[200px] font-mono"
                />
              </div>
            </div>
            <DialogFooter>
              <Button onClick={handleCreateProcess}>Criar Processo</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>

      {/* Cards de Estatísticas */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total de Processos</CardTitle>
            <Code className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{processes.length}</div>
            <p className="text-xs text-muted-foreground">
              {processes.filter((p) => p.status === "ACTIVE").length} ativos
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Execuções Hoje</CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{executions.length}</div>
            <p className="text-xs text-muted-foreground">
              {executions.filter((e) => e.status === "SUCCESS").length} sucessos
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Em Execução</CardTitle>
            <Play className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{executions.filter((e) => e.status === "RUNNING").length}</div>
            <p className="text-xs text-muted-foreground">processos rodando</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Taxa de Sucesso</CardTitle>
            <Database className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {executions.length > 0
                ? Math.round((executions.filter((e) => e.status === "SUCCESS").length / executions.length) * 100)
                : 0}
              %
            </div>
            <p className="text-xs text-muted-foreground">últimas 24h</p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="processes" className="space-y-4">
        <TabsList>
          <TabsTrigger value="processes">Processos</TabsTrigger>
          <TabsTrigger value="executions">Execuções</TabsTrigger>
          <TabsTrigger value="schedules">Agendamentos</TabsTrigger>
        </TabsList>

        <TabsContent value="processes" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Processos RPA</CardTitle>
              <CardDescription>Gerencie seus processos de automação</CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Nome</TableHead>
                    <TableHead>Tipo</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Agendamento</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {processes.map((process) => (
                    <TableRow key={process.id}>
                      <TableCell>
                        <div>
                          <div className="font-medium">{process.name}</div>
                          <div className="text-sm text-muted-foreground">{process.description}</div>
                        </div>
                      </TableCell>
                      <TableCell>
                        <Badge variant="outline">{process.scriptType}</Badge>
                      </TableCell>
                      <TableCell>
                        <Badge variant={process.status === "ACTIVE" ? "default" : "secondary"}>{process.status}</Badge>
                      </TableCell>
                      <TableCell>
                        {process.cronExpression ? (
                          <div className="flex items-center">
                            <Calendar className="w-4 h-4 mr-1" />
                            <span className="text-sm">{process.cronExpression}</span>
                          </div>
                        ) : (
                          <span className="text-muted-foreground">Manual</span>
                        )}
                      </TableCell>
                      <TableCell>
                        <div className="flex space-x-2">
                          <Button size="sm" onClick={() => handleExecuteProcess(process.id)}>
                            <Play className="w-4 h-4" />
                          </Button>
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

        <TabsContent value="executions" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Histórico de Execuções</CardTitle>
              <CardDescription>Acompanhe o status das execuções dos processos</CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Início</TableHead>
                    <TableHead>Duração</TableHead>
                    <TableHead>Tipo</TableHead>
                    <TableHead>Resultado</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {executions.map((execution) => (
                    <TableRow key={execution.id}>
                      <TableCell>#{execution.id}</TableCell>
                      <TableCell>{getStatusBadge(execution.status)}</TableCell>
                      <TableCell>{new Date(execution.startTime).toLocaleString()}</TableCell>
                      <TableCell>{formatDuration(execution.durationMs)}</TableCell>
                      <TableCell>
                        <Badge variant="outline">{execution.triggeredBy}</Badge>
                      </TableCell>
                      <TableCell>
                        {execution.resultMessage && (
                          <span className="text-green-600 text-sm">{execution.resultMessage}</span>
                        )}
                        {execution.errorMessage && (
                          <span className="text-red-600 text-sm">{execution.errorMessage}</span>
                        )}
                        {execution.status === "RUNNING" && (
                          <span className="text-blue-600 text-sm">Em execução...</span>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="schedules" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Agendamentos</CardTitle>
              <CardDescription>Configure quando seus processos devem ser executados</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {processes
                  .filter((p) => p.cronExpression)
                  .map((process) => (
                    <div key={process.id} className="flex items-center justify-between p-4 border rounded-lg">
                      <div>
                        <h3 className="font-medium">{process.name}</h3>
                        <p className="text-sm text-muted-foreground">Expressão Cron: {process.cronExpression}</p>
                      </div>
                      <div className="flex items-center space-x-2">
                        <Badge variant="default">Ativo</Badge>
                        <Button size="sm" variant="outline">
                          <Edit className="w-4 h-4" />
                        </Button>
                      </div>
                    </div>
                  ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}
