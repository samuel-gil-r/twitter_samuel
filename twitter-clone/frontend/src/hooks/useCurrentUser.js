import { useState, useEffect } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import { getCurrentUser } from '../api/user.js'

export function useCurrentUser() {
  const { isAuthenticated } = useAuth0()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!isAuthenticated) return
    setLoading(true)
    getCurrentUser()
      .then(setProfile)
      .catch(() => setError('Failed to load profile.'))
      .finally(() => setLoading(false))
  }, [isAuthenticated])

  return { profile, loading, error }
}
