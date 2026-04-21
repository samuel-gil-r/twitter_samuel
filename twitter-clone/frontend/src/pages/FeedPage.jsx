import { useAuth0 } from '@auth0/auth0-react'
import { useFeed } from '../hooks/useFeed.js'
import PostList from '../components/PostList.jsx'
import CreatePostForm from '../components/CreatePostForm.jsx'
import RefreshButton from '../components/RefreshButton.jsx'

export default function FeedPage() {
  const { isAuthenticated } = useAuth0()
  const { posts, loading, error, refresh } = useFeed()

  return (
    <div className="feed-page">
      <div className="feed-header">
        <h1>Feed Global</h1>
        <RefreshButton refresh={refresh} />
      </div>
      {isAuthenticated && <CreatePostForm onSuccess={refresh} />}
      <PostList posts={posts} loading={loading} error={error} />
    </div>
  )
}
