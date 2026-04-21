import { useState } from 'react'
import { createPost } from '../api/posts.js'

const MAX = 140

export default function CreatePostForm({ onSuccess }) {
  const [content, setContent] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(false)

  const remaining = MAX - content.length

  async function handleSubmit(e) {
    e.preventDefault()
    if (!content.trim()) return
    setLoading(true)
    setError(null)
    setSuccess(false)
    try {
      await createPost(content.trim())
      setContent('')
      setSuccess(true)
      setTimeout(() => setSuccess(false), 2000)
      onSuccess?.()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create post.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form className="create-post-form" onSubmit={handleSubmit}>
      <textarea
        className="post-textarea"
        placeholder="¿Qué está pasando? (máx. 140 caracteres)"
        value={content}
        onChange={(e) => setContent(e.target.value)}
        maxLength={MAX}
        rows={3}
      />
      <div className="post-form-footer">
        <span className={`char-counter ${remaining <= 20 ? 'char-counter--warning' : ''}`}>
          {remaining}
        </span>
        <button className="btn btn-primary" type="submit" disabled={loading || !content.trim()}>
          {loading ? 'Publicando…' : 'Publicar'}
        </button>
      </div>
      {error && <p className="error-message">{error}</p>}
      {success && <p className="success-message">¡Publicado!</p>}
    </form>
  )
}
