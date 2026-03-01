"use client"

import { useState, useMemo } from "react"
import { Plus, Search, Pencil, Trash2, Mail, Phone } from "lucide-react"
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
} from "@/components/ui/dialog"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogDescription,
} from "@/components/ui/alert-dialog"
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
import { useAuthorities } from "@/hooks/use-data"
import { authorityApi } from "@/lib/api"
import type { Authority } from "@/lib/api"

export default function AuthoritiesPage() {
  const { authorities, isLoading, mutate } = useAuthorities()
  const [search, setSearch] = useState("")
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteOpen, setDeleteOpen] = useState(false)
  const [editingAuthority, setEditingAuthority] = useState<Authority | null>(null)
  const [deletingAuthority, setDeletingAuthority] = useState<Authority | null>(null)
  const [formData, setFormData] = useState({
    name: "",
    designation: "",
    contactNumber: "",
    email: "",
  })

  const filtered = useMemo(() => {
    if (!search) return authorities

    const q = search.toLowerCase().trim()

    return authorities.filter((a) => {
      return (
        (a.name?.toLowerCase().includes(q) ?? false) ||
        (a.designation?.toLowerCase().includes(q) ?? false) ||
        (a.email?.toLowerCase().includes(q) ?? false) ||
        (a.contactNumber?.toLowerCase().includes(q) ?? false)
      )
    })
  }, [authorities, search])



  function openAdd() {
    setEditingAuthority(null)
    setFormData({ name: "", designation: "", contactNumber: "", email: "" })
    setDialogOpen(true)
  }

  function openEdit(authority: Authority) {
    setEditingAuthority(authority)
    setFormData({
      name: authority.name,
      designation: authority.designation,
      contactNumber: authority.contactNumber,
      email: authority.email,
    })
    setDialogOpen(true)
  }

  function openDelete(authority: Authority) {
    setDeletingAuthority(authority)
    setDeleteOpen(true)
  }

  // Save to backend like stations page
  async function handleSave() {
    try {
      if (editingAuthority) {
        await authorityApi.update(editingAuthority.authorityId, formData)
      } else {
        await authorityApi.create(formData)
      }
      await mutate() // refresh list
      setDialogOpen(false)
      setEditingAuthority(null)
      setFormData({ name: "", designation: "", contactNumber: "", email: "" }) // <-- ADD THIS
    } catch (err: any) {
      const message =
        err.response?.data?.message ||
        err.message ||
        "Failed to save authority"
      alert(message) // or use toast/snackbar
    }
  }


  // Delete from backend
  async function handleDelete() {
    if (!deletingAuthority) return

    try {
      await authorityApi.delete(deletingAuthority.authorityId)
      await mutate() // refresh list after successful deletion
      setDeleteOpen(false)
      setDeletingAuthority(null)
    } catch (err: any) {
      // Extract message from backend (Spring Boot BadRequestException)
      const message =
        err.response?.data?.message ||
        err.message ||
        "Failed to delete authority"
      alert(message) // or replace with toast/snackbar for better UX
      // keep delete dialog open so user can read message
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
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">Authority Management</h1>
          <p className="text-sm text-muted-foreground">
            Manage responsible personnel and departments
          </p>
        </div>
        <Button onClick={openAdd} className="gap-2">
          <Plus className="size-4" />
          Add Authority
        </Button>
      </div>

      <Card>
        <CardHeader>
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <CardTitle className="text-base">All Authorities</CardTitle>
              <CardDescription>{authorities.length} authorities registered</CardDescription>
            </div>
            <div className="relative w-full sm:w-72">
              <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder="Search authorities..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-9"
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="w-full overflow-x-auto">
            <Table className="min-w-[950px]">
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Name</TableHead>
                <TableHead>Designation</TableHead>
                <TableHead>Contact</TableHead>
                <TableHead>Email</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filtered.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} className="h-32 text-center text-muted-foreground">
                    No authorities found
                  </TableCell>
                </TableRow>
              ) : (
                filtered.map((authority) => (
                  <TableRow key={authority.authorityId}>
                    <TableCell className="font-mono text-xs text-muted-foreground">
                      #{authority.authorityId}
                    </TableCell>
                    <TableCell className="font-medium text-card-foreground">{authority.name}</TableCell>
                    <TableCell>
                      <Badge variant="secondary">{authority.designation}</Badge>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center gap-1.5 text-sm text-muted-foreground">
                        <Phone className="size-3" />
                        {authority.contactNumber}
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center gap-1.5 text-sm text-muted-foreground">
                        <Mail className="size-3" />
                        {authority.email}
                      </div>
                    </TableCell>
                    <TableCell className="text-right whitespace-nowrap">
                      <div className="flex items-center justify-end gap-1">
                        <Button variant="ghost" size="icon" onClick={() => openEdit(authority)} className="size-8">
                          <Pencil className="size-3.5" />
                          <span className="sr-only">Edit {authority.name}</span>
                        </Button>
                        <Button variant="ghost" size="icon" onClick={() => openDelete(authority)} className="size-8 text-destructive hover:text-destructive">
                          <Trash2 className="size-3.5" />
                          <span className="sr-only">Delete {authority.name}</span>
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
            <DialogTitle>{editingAuthority ? "Edit Authority" : "Add Authority"}</DialogTitle>
          </DialogHeader>
          <div className="flex flex-col gap-4 py-4">
            <div className="flex flex-col gap-2">
              <Label htmlFor="name">Full Name</Label>
              <Input
                id="name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="e.g., Rajesh Kumar"
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="designation">Designation</Label>
              <Input
                id="designation"
                value={formData.designation}
                onChange={(e) => setFormData({ ...formData, designation: e.target.value })}
                placeholder="e.g., Chief Engineer"
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="contactNumber">Contact Number</Label>
              <Input
                id="contactNumber"
                value={formData.contactNumber}
                onChange={(e) => setFormData({ ...formData, contactNumber: e.target.value })}
                placeholder="e.g., +91 98765 43210"
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="e.g., name@dmrc.org"
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Cancel
            </Button>
            <Button
              onClick={handleSave}
              disabled={!formData.name || !formData.designation || !formData.email}
            >
              {editingAuthority ? "Save Changes" : "Add Authority"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation */}
      <AlertDialog open={deleteOpen} onOpenChange={setDeleteOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Authority</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete &quot;{deletingAuthority?.name}&quot;? This action cannot be undone.
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
