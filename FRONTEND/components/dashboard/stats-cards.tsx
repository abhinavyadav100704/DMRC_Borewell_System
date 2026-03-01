"use client"

import { TrainFront, Droplets, CheckCircle, XCircle, Users, TrendingUp } from "lucide-react"
import { Card, CardContent } from "@/components/ui/card"

interface StatsCardsProps {
  totalStations: number
  totalBorewells: number
  activeBorewells: number
  inactiveBorewells: number
  totalAuthorities: number
}

export function StatsCards({
  totalStations,
  totalBorewells,
  activeBorewells,
  inactiveBorewells,
  totalAuthorities,
}: StatsCardsProps) {
  const stats = [
    {
      label: "Total Stations",
      value: totalStations,
      icon: TrainFront,
      color: "bg-primary/10 text-primary",
      trend: "+2 this month",
    },
    {
      label: "Total Borewells",
      value: totalBorewells,
      icon: Droplets,
      color: "bg-chart-2/10 text-chart-2",
      trend: "+3 this month",
    },
    {
      label: "Active Borewells",
      value: activeBorewells,
      icon: CheckCircle,
      color: "bg-chart-2/10 text-chart-2",
      trend: `${((activeBorewells / totalBorewells) * 100).toFixed(0)}% operational`,
    },
    {
      label: "Inactive Borewells",
      value: inactiveBorewells,
      icon: XCircle,
      color: "bg-destructive/10 text-destructive",
      trend: "Needs attention",
    },
    {
      label: "Total Authorities",
      value: totalAuthorities,
      icon: Users,
      color: "bg-chart-4/10 text-chart-4",
      trend: "All assigned",
    },
  ]

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
      {stats.map((stat) => (
        <Card key={stat.label} className="relative overflow-hidden">
          <CardContent className="p-4">
            <div className="flex items-start justify-between">
              <div className="flex flex-col gap-1">
                <span className="text-xs font-medium text-muted-foreground">{stat.label}</span>
                <span className="text-2xl font-bold text-card-foreground">{stat.value}</span>
              </div>
              <div className={`flex size-9 items-center justify-center rounded-lg ${stat.color}`}>
                <stat.icon className="size-4" />
              </div>
            </div>
            <div className="mt-2 flex items-center gap-1 text-xs text-muted-foreground">
              <TrendingUp className="size-3" />
              <span>{stat.trend}</span>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
