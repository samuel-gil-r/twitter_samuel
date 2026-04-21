import { useState, useEffect, useCallback } from 'react'
import { getPosts } from '../api/posts.js'

export function useFeed() {
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const fetchPosts = useCallback(async () => {
    try {
      setError(null)
      const data = await getPosts()
      setPosts(data)
    } catch (err) {
      setError('Failed to load posts. Please try again.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchPosts()
    const interval = setInterval(fetchPosts, 30_000)
    return () => clearInterval(interval)
  }, [fetchPosts])

  return { posts, loading, error, refresh: fetchPosts }
}
