import PostCard from './PostCard.jsx'

export default function PostList({ posts, loading, error }) {
  if (loading) {
    return (
      <div className="post-list">
        {[1, 2, 3].map((i) => (
          <div key={i} className="post-card post-card--skeleton" aria-hidden="true">
            <div className="post-card-inner">
              <div className="skeleton-avatar" />
              <div className="post-body">
                <div className="skeleton-line" style={{ width: '35%', marginBottom: '0.5rem' }} />
                <div className="skeleton-line" style={{ width: '85%' }} />
                <div className="skeleton-line" style={{ width: '60%' }} />
              </div>
            </div>
          </div>
        ))}
      </div>
    )
  }

  if (error) {
    return <p className="error-message">{error}</p>
  }

  if (posts.length === 0) {
    return <p className="empty-message">Aún no hay publicaciones. ¡Sé el primero!</p>
  }

  return (
    <div className="post-list">
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  )
}
