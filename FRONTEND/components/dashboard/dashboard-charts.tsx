"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from "recharts"
import type { Borewell, Station } from "@/lib/api"

interface DashboardChartsProps {
  borewells: Borewell[]
  stations: Station[]
}

export function DashboardCharts({ borewells, stations }: DashboardChartsProps) {
  // Borewells by station
  const borewellsByStation = stations.map((station) => ({
    name: station.stationName.length > 12 ? station.stationName.slice(0, 12) + "..." : station.stationName,
    count: borewells.filter((bw) => bw.station?.stationId === station.stationId).length,
  })).filter(item => item.count > 0)

  // Active vs inactive pie data
  const activeBorewells = borewells.filter((bw) => bw.isAvailable).length
  const inactiveBorewells = borewells.filter((bw) => !bw.isAvailable).length
  const statusData = [
    { name: "Active", value: activeBorewells },
    { name: "Inactive", value: inactiveBorewells },
  ]

  // Depth distribution
  const depthRanges = [
    { range: "100-150m", min: 100, max: 150 },
    { range: "151-180m", min: 151, max: 180 },
    { range: "181-210m", min: 181, max: 210 },
    { range: "211-250m", min: 211, max: 250 },
  ]
  const depthData = depthRanges.map((dr) => ({
    range: dr.range,
    count: borewells.filter((bw) => bw.depth >= dr.min && bw.depth <= dr.max).length,
  }))

  const PIE_COLORS = ["oklch(0.55 0.16 165)", "oklch(0.577 0.245 27.325)"]

  return (
    <div className="grid grid-cols-1 gap-4 lg:grid-cols-2 xl:grid-cols-3">
      {/* Borewells per station */}
      <Card className="xl:col-span-2">
        <CardHeader>
          <CardTitle className="text-base">Borewells per Station</CardTitle>
          <CardDescription>Distribution of borewells across metro stations</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="h-[280px]">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={borewellsByStation} margin={{ top: 5, right: 10, left: -10, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-border" />
                <XAxis
                  dataKey="name"
                  tick={{ fontSize: 11 }}
                  className="fill-muted-foreground"
                />
                <YAxis
                  tick={{ fontSize: 11 }}
                  className="fill-muted-foreground"
                  allowDecimals={false}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "oklch(1 0 0)",
                    border: "1px solid oklch(0.90 0.01 250)",
                    borderRadius: "8px",
                    fontSize: "12px",
                  }}
                />
                <Bar
                  dataKey="count"
                  fill="oklch(0.45 0.18 255)"
                  radius={[4, 4, 0, 0]}
                  name="Borewells"
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>

      {/* Active/Inactive pie chart */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Borewell Status</CardTitle>
          <CardDescription>Active vs Inactive borewells</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="h-[280px] flex items-center justify-center">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={statusData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={85}
                  dataKey="value"
                  labelLine={false}
                >
                  {statusData.map((_, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={PIE_COLORS[index % PIE_COLORS.length]}
                    />
                  ))}
                </Pie>

                {/* Center Text */}
                <text
                  x="50%"
                  y="45%"
                  textAnchor="middle"
                  dominantBaseline="middle"
                  className="fill-foreground text-sm font-semibold"
                >
                  {activeBorewells + inactiveBorewells}
                </text>

                <text
                  x="50%"
                  y="55%"
                  textAnchor="middle"
                  dominantBaseline="middle"
                  className="fill-muted-foreground text-xs"
                >
                  Total Borewells
                </text>

                <Legend verticalAlign="bottom" height={36} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "oklch(1 0 0)",
                    border: "1px solid oklch(0.90 0.01 250)",
                    borderRadius: "8px",
                    fontSize: "12px",
                  }}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>


      {/* Depth distribution */}
      <Card className="lg:col-span-2 xl:col-span-3">
        <CardHeader>
          <CardTitle className="text-base">Borewell Depth Distribution</CardTitle>
          <CardDescription>Number of borewells across different depth ranges</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="h-[220px]">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={depthData} margin={{ top: 5, right: 10, left: -10, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-border" />
                <XAxis
                  dataKey="range"
                  tick={{ fontSize: 11 }}
                  className="fill-muted-foreground"
                />
                <YAxis
                  tick={{ fontSize: 11 }}
                  className="fill-muted-foreground"
                  allowDecimals={false}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "oklch(1 0 0)",
                    border: "1px solid oklch(0.90 0.01 250)",
                    borderRadius: "8px",
                    fontSize: "12px",
                  }}
                />
                <Bar
                  dataKey="count"
                  fill="oklch(0.55 0.16 165)"
                  radius={[4, 4, 0, 0]}
                  name="Borewells"
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
