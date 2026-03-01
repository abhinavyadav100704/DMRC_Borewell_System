import type { Station, Borewell, Authority } from "./api"

export const demoStations: Station[] = [
  { id: 1, stationName: "Rajiv Chowk", location: "Central Delhi" },
  { id: 2, stationName: "Kashmere Gate", location: "North Delhi" },
  { id: 3, stationName: "Huda City Centre", location: "Gurgaon" },
  { id: 4, stationName: "Dwarka Sector 21", location: "West Delhi" },
  { id: 5, stationName: "Noida City Centre", location: "Noida" },
  { id: 6, stationName: "Botanical Garden", location: "Noida" },
  { id: 7, stationName: "Vaishali", location: "Ghaziabad" },
  { id: 8, stationName: "Chandni Chowk", location: "Old Delhi" },
  { id: 9, stationName: "Hauz Khas", location: "South Delhi" },
  { id: 10, stationName: "Saket", location: "South Delhi" },
]

export const demoBorewells: Borewell[] = [
  { id: 1, borewellNumber: "BW-001", depth: 150, status: "Active", station: demoStations[0] },
  { id: 2, borewellNumber: "BW-002", depth: 200, status: "Active", station: demoStations[0] },
  { id: 3, borewellNumber: "BW-003", depth: 180, status: "Inactive", station: demoStations[1] },
  { id: 4, borewellNumber: "BW-004", depth: 220, status: "Active", station: demoStations[2] },
  { id: 5, borewellNumber: "BW-005", depth: 160, status: "Active", station: demoStations[3] },
  { id: 6, borewellNumber: "BW-006", depth: 190, status: "Inactive", station: demoStations[4] },
  { id: 7, borewellNumber: "BW-007", depth: 175, status: "Active", station: demoStations[5] },
  { id: 8, borewellNumber: "BW-008", depth: 210, status: "Active", station: demoStations[6] },
  { id: 9, borewellNumber: "BW-009", depth: 145, status: "Inactive", station: demoStations[7] },
  { id: 10, borewellNumber: "BW-010", depth: 230, status: "Active", station: demoStations[8] },
  { id: 11, borewellNumber: "BW-011", depth: 165, status: "Active", station: demoStations[9] },
  { id: 12, borewellNumber: "BW-012", depth: 195, status: "Active", station: demoStations[1] },
  { id: 13, borewellNumber: "BW-013", depth: 140, status: "Inactive", station: demoStations[3] },
  { id: 14, borewellNumber: "BW-014", depth: 250, status: "Active", station: demoStations[5] },
  { id: 15, borewellNumber: "BW-015", depth: 185, status: "Active", station: demoStations[7] },
]

export const demoAuthorities: Authority[] = [
  { id: 1, name: "Rajesh Kumar", designation: "Chief Engineer", contactNumber: "+91 98765 43210", email: "rajesh.kumar@dmrc.org" },
  { id: 2, name: "Priya Sharma", designation: "Station Manager", contactNumber: "+91 98765 43211", email: "priya.sharma@dmrc.org" },
  { id: 3, name: "Amit Singh", designation: "Maintenance Head", contactNumber: "+91 98765 43212", email: "amit.singh@dmrc.org" },
  { id: 4, name: "Sunita Verma", designation: "Water Resource Officer", contactNumber: "+91 98765 43213", email: "sunita.verma@dmrc.org" },
  { id: 5, name: "Vikram Patel", designation: "Site Supervisor", contactNumber: "+91 98765 43214", email: "vikram.patel@dmrc.org" },
  { id: 6, name: "Anita Gupta", designation: "Environmental Officer", contactNumber: "+91 98765 43215", email: "anita.gupta@dmrc.org" },
]
