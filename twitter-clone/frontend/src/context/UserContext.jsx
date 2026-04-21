import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import { getCurrentUser } from '../api/user.js'

const UserContext = createContext(null)

export function UserProvider({ children }) {
  const { isAuthenticated } = useAuth0()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const fetchProfile = useCallback(() => {
    if (!isAuthenticated) { setProfile(null); return }
    setLoading(true)
    getCurrentUser()
      .then(setProfile)
      .catch(() => setError('No se pudo cargar el perfil.'))
      .finally(() => setLoading(false))
  }, [isAuthenticated])

  useEffect(() => { fetchProfile() }, [fetchProfile])

  return (
    <UserContext.Provider value={{ profile, loading, error, refreshProfile: fetchProfile, setProfile }}>
      {children}
    </UserContext.Provider>
  )
}

export function useUser() {
  return useContext(UserContext)
}
