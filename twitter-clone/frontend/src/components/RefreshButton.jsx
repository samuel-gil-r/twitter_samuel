import { useState } from 'react'

export default function RefreshButton({ refresh }) {
  const [refreshed, setRefreshed] = useState(false)

  async function handleClick() {
    await refresh()
    setRefreshed(true)
    setTimeout(() => setRefreshed(false), 1500)
  }

  return (
    <button className="refresh-btn" onClick={handleClick} title="Actualizar">
      {refreshed ? '✓' : '↻'}
    </button>
  )
}
