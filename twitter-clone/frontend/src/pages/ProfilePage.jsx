import { useUser } from '../context/UserContext.jsx'

export default function ProfilePage() {
  const { profile, loading, error } = useUser()

  if (loading) return <div className="loading">Cargando perfil…</div>
  if (error) return <p className="error-message">{error}</p>
  if (!profile) return null

  const joined = new Date(profile.createdAt).toLocaleDateString()

  return (
    <div className="profile-card">
      <h2>Mi Perfil</h2>
      <p><strong>Nombre:</strong> @{profile.username}</p>
      <p><strong>Email:</strong> {profile.email}</p>
      <p><strong>Miembro desde:</strong> {joined}</p>
    </div>
  )
}
