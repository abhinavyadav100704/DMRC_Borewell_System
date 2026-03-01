"use client"

import { useState, useMemo } from "react"
import { Plus, Search, Pencil, Trash2, Filter } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Skeleton } from "@/components/ui/skeleton"
import { useBorewells, useStations, useAuthorities } from "@/hooks/use-data"
import { borewellApi } from "@/lib/api"
import type { Borewell, Station } from "@/lib/api"

// ---------- Type for form data ----------
interface BorewellFormData {
  borewellNo: number | ""
  depth: number | ""
  isAvailable: boolean
  stationId: number | ""
  authorityId?: number | ""
  distanceM?: number
  diameter?: number
  location?: string
  approvalDate?: string
}

export default function BorewellsPage() {
  const { borewells, isLoading, mutate } = useBorewells()
  const { stations } = useStations()
  const { authorities } = useAuthorities()
  const [search, setSearch] = useState("")
  const [statusFilter, setStatusFilter] = useState("all")
  const [stationFilter, setStationFilter] = useState("all")
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteOpen, setDeleteOpen] = useState(false)
  const [editingBorewell, setEditingBorewell] = useState<Borewell | null>(null)
  const [deletingBorewell, setDeletingBorewell] = useState<Borewell | null>(null)
  const [formData, setFormData] = useState<BorewellFormData>({
    borewellNo: "",
    depth: "",
    isAvailable: true,
    stationId: "",
    authorityId: "",
  })


  // ---------- Filtered borewells ----------
  // ---------- Filtered borewells ----------
const filtered = useMemo(() => {
    return borewells.filter((bw) => {
      // 1️⃣ Search filter
      const q = search.toLowerCase().trim()
      const matchesSearch =
        !search ||
        bw.borewellNo.toString().includes(q) ||
        bw.station?.stationName?.toLowerCase().includes(q) ||
        bw.authority?.name?.toLowerCase().includes(q)

      // 2️⃣ Status filter
      const matchesStatus =
        statusFilter === "all" ||
        (bw.isAvailable ? "Active" : "Inactive") === statusFilter

      // 3️⃣ Station filter
      const matchesStation =
        stationFilter === "all" ||
        bw.station?.stationId === Number(stationFilter)

      return matchesSearch && matchesStatus && matchesStation
    })
  }, [borewells, search, statusFilter, stationFilter])


  // ---------- Open add dialog ----------
  function openAdd() {
    setEditingBorewell(null)
    setFormData({ borewellNo: "", depth: "", isAvailable: true, stationId: "", authorityId: "" })
    setDialogOpen(true)
  }

  // ---------- Open edit dialog ----------
  function openEdit(borewell: Borewell) {
    setEditingBorewell(borewell)
    setFormData({
      borewellNo: borewell.borewellNo || "",
      depth: borewell.depth || "",
      isAvailable: borewell.isAvailable ?? true,
      stationId: borewell.station?.stationId || "",
      authorityId: borewell.authority?.authorityId || "",
      distanceM: borewell.distanceM,
      diameter: borewell.diameter,
      location: borewell.location,
      approvalDate: borewell.approvalDate
        ? borewell.approvalDate.toString()
        : undefined,
  })

    setDialogOpen(true)
  }

  // ---------- Open delete dialog ----------
  function openDelete(borewell: Borewell) {
    setDeletingBorewell(borewell)
    setDeleteOpen(true)
  }

  // ---------- Save borewell (create or update) ----------
  async function handleSave() {
    try {
      const selectedStation = stations.find((s) => s.stationId === Number(formData.stationId))
      if (!selectedStation) {
        alert("Please select a valid station")
        return
      }
      const selectedAuthority = authorities.find(
        (a) => a.authorityId === Number(formData.authorityId)
      )

      const payload: Partial<Borewell> = {
        borewellNo: Number(formData.borewellNo),
        depth: Number(formData.depth),
        isAvailable: formData.isAvailable,
        station: selectedStation,
        authority: selectedAuthority,

        distanceM: formData.distanceM,
        diameter: formData.diameter,
        location: formData.location,
        approvalDate: formData.approvalDate ? new Date(formData.approvalDate) : undefined,
      }

      if (editingBorewell) {
        await borewellApi.update(editingBorewell.borewellId, payload)
      } else {
        await borewellApi.create(payload)
      }

      await mutate()
      setDialogOpen(false)
    } catch (err: any) {
      alert("Error saving borewell: " + err.message)
    }
  }

  // ---------- Delete borewell ----------
  async function handleDelete() {
    try {
      if (deletingBorewell) {
        await borewellApi.delete(deletingBorewell.borewellId)
        await mutate()
      }
      setDeleteOpen(false)
      setDeletingBorewell(null)
    } catch (err: any) {
      alert("Error deleting borewell: " + err.message)
    }
  }

  if (isLoading) {
    return (
      <div className="flex flex-col gap-6">
        <Skeleton className="h-7 w-48" />
        <Skeleton className="h-96 rounded-xl" />
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">Borewell Management</h1>
          <p className="text-sm text-muted-foreground">
            Monitor and manage borewells across all stations
          </p>
        </div>
        <Button onClick={openAdd} className="gap-2">
          <Plus className="size-4" /> Add Borewell
        </Button>
      </div>

      {/* Filters & Table */}
      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <CardTitle className="text-base">All Borewells</CardTitle>
                <CardDescription>{borewells.length} borewells registered</CardDescription>
              </div>
              <div className="relative w-full sm:w-72">
                <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  placeholder="Search borewells..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  className="pl-9"
                />
              </div>
            </div>
            <div className="flex flex-wrap items-center gap-2">
              <Filter className="size-4 text-muted-foreground" />
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger className="w-36"><SelectValue placeholder="Status" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Status</SelectItem>
                  <SelectItem value="Active">Active</SelectItem>
                  <SelectItem value="Inactive">Inactive</SelectItem>
                </SelectContent>
              </Select>
              <Select value={stationFilter} onValueChange={setStationFilter}>
                <SelectTrigger className="w-48"><SelectValue placeholder="Station" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Stations</SelectItem>
                  {stations.map((s) => (
                    <SelectItem key={s.stationId} value={s.stationId.toString()}>{s.stationName}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {(statusFilter !== "all" || stationFilter !== "all") && (
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => { setStatusFilter("all"); setStationFilter("all") }}
                  className="text-xs"
                >
                  Clear filters
                </Button>
              )}
            </div>
          </div>
        </CardHeader>

        <CardContent>
          <div className="w-full overflow-x-auto">
            <Table className="min-w-[900px]">
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Borewell No.</TableHead>
                <TableHead>Station</TableHead>
                <TableHead>Authority</TableHead>
                <TableHead>Depth (m)</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filtered.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} className="h-32 text-center text-muted-foreground">
                    No borewells found
                  </TableCell>
                </TableRow>
              ) : (
                filtered.map((bw) => (
                  <TableRow key={bw.borewellId}>
                    <TableCell className="font-mono text-xs text-muted-foreground">#{bw.borewellId}</TableCell>
                    <TableCell className="font-medium text-card-foreground">{bw.borewellNo}</TableCell>
                    <TableCell className="text-muted-foreground">{bw.station?.stationName || "Unassigned"}</TableCell>
                    <TableCell className="text-muted-foreground">{bw.authority?.name || "N/A"}</TableCell>
                    <TableCell className="font-mono text-sm">{bw.depth}m</TableCell>
                    <TableCell>
                      <Badge variant={bw.isAvailable ? "default" : "destructive"}>
                        {bw.isAvailable ? "Active" : "Inactive"}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right whitespace-nowrap">
                      <div className="flex items-center justify-end gap-1">
                        <Button variant="ghost" size="icon" onClick={() => openEdit(bw)}>
                          <Pencil className="size-3.5" />
                          <span className="sr-only">Edit {bw.borewellNo}</span>
                        </Button>
                        <Button variant="ghost" size="icon" onClick={() => openDelete(bw)} className="text-destructive">
                          <Trash2 className="size-3.5" />
                          <span className="sr-only">Delete {bw.borewellNo}</span>
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
          </div>
        </CardContent>
      </Card>

      {/* Add/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editingBorewell ? "Edit Borewell" : "Add Borewell"}</DialogTitle>
            <DialogDescription>
              {editingBorewell ? "Update the borewell details below." : "Fill in the details to add a new borewell."}
            </DialogDescription>
          </DialogHeader>
          <div className="flex flex-col gap-4 py-4">
            <div className="flex flex-col gap-2">
              <Label htmlFor="borewellNo">Borewell Number</Label>
              <Input
                id="borewellNo"
                type="number"
                value={formData.borewellNo.toString()}
                onChange={(e) => setFormData({ ...formData, borewellNo: e.target.value === "" ? "" : Number(e.target.value) })}
                placeholder="e.g., 16"
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="depth">Depth (meters)</Label>
              <Input
                id="depth"
                type="number"
                value={formData.depth.toString()}
                onChange={(e) => setFormData({ ...formData, depth: e.target.value === "" ? "" : Number(e.target.value) })}
                placeholder="e.g., 200"
              />
            </div>
            <div className="flex items-center gap-2 pt-2">
              <input
                type="checkbox"
                checked={formData.isAvailable}
                onChange={(e) =>
                  setFormData({ ...formData, isAvailable: e.target.checked })
                }
              />
              <Label>Available</Label>
            </div>

            <div className="flex flex-col gap-2">
              <Label htmlFor="station">Station</Label>
              <Select value={formData.stationId.toString()} onValueChange={(v) => setFormData({ ...formData, stationId: v === "" ? "" : Number(v) })}>
                <SelectTrigger><SelectValue placeholder="Select a station" /></SelectTrigger>
                <SelectContent>
                  {stations.map((s) => (
                    <SelectItem key={s.stationId} value={s.stationId.toString()}>{s.stationName}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="flex flex-col gap-2">
              <Label>Authority</Label>
              <Select
                value={formData.authorityId?.toString()}
                onValueChange={(v) => setFormData({ ...formData, authorityId: v === "" ? "" : Number(v) })}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select an authority" />
                </SelectTrigger>
                <SelectContent>
                  {authorities.map((a) => (
                    <SelectItem
                      key={a.authorityId}
                      value={a.authorityId.toString()}
                    >
                      {a.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>Cancel</Button>
            <Button
              onClick={handleSave}
              disabled={!formData.borewellNo || !formData.depth || !formData.stationId}
            >
              {editingBorewell ? "Save Changes" : "Add Borewell"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation */}
      <AlertDialog open={deleteOpen} onOpenChange={setDeleteOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Borewell</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete &quot;{deletingBorewell?.borewellNo}&quot;? This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              className="bg-destructive text-white hover:bg-destructive/90"
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
