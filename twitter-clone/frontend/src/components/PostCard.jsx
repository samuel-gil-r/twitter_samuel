export default function PostCard({ post }) {
  const date = new Date(post.createdAt).toLocaleString()
  const initials = post.authorUsername
    ? post.authorUsername.slice(0, 2).toUpperCase()
    : '?'

  return (
    <div className="post-card">
      <div className="post-card-inner">
        <div className="post-avatar">{initials}</div>
        <div className="post-body">
          <div className="post-header">
            <span className="post-author">@{post.authorUsername}</span>
            <span className="post-date">{date}</span>
          </div>
          <p className="post-content">{post.content}</p>
        </div>
      </div>
    </div>
  )
}
