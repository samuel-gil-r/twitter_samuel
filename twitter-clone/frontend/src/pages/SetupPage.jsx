import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { updateDisplayName } from '../api/user.js'
import { useUser } from '../context/UserContext.jsx'

export default function SetupPage() {
  const [name, setName] = useState('')
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)
  const { setProfile } = useUser()
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    const trimmed = name.trim()
    if (!trimmed) { setError('Ingresa tu nombre.'); return }
    if (trimmed.length > 50) { setError('Máximo 50 caracteres.'); return }

    setSaving(true)
    setError(null)
    try {
      const updated = await updateDisplayName(trimmed)
      setProfile(updated)
      navigate('/')
    } catch (err) {
      const msg = err.response?.data?.message
      setError(msg || 'No se pudo guardar. Intenta de nuevo.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="setup-container">
      <div className="setup-card">
        <h2>¡Bienvenido!</h2>
        <p className="setup-subtitle">¿Cómo quieres que te vean los demás?</p>
        <form onSubmit={handleSubmit} className="setup-form">
          <input
            className="setup-input"
            type="text"
            placeholder="Tu nombre"
            value={name}
            onChange={e => setName(e.target.value)}
            maxLength={50}
            autoFocus
          />
          <span className="setup-counter">{name.length}/50</span>
          {error && <p className="error-message">{error}</p>}
          <button
            type="submit"
            className="btn btn-primary setup-btn"
            disabled={saving || !name.trim()}
          >
            {saving ? 'Guardando…' : 'Continuar'}
          </button>
        </form>
      </div>
    </div>
  )
}
