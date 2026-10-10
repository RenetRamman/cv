import { CvPage } from './components/CvPage'
import { useCvData } from './hooks/useCvData'
import './styles/cv.css'

function App() {
  const cvState = useCvData()

  if (cvState.status === 'loading') {
    return (
      <div className="cv-status" role="status">
        Loading CV…
      </div>
    )
  }

  if (cvState.status === 'error') {
    return (
      <div className="cv-status cv-status--error" role="alert">
        <p>Could not load CV data from the backend.</p>
        <p className="cv-status__detail">{cvState.message}</p>
        <p className="cv-status__hint">
          Make sure PostgreSQL and the Spring Boot API are running, then refresh.
        </p>
      </div>
    )
  }

  return <CvPage data={cvState.data} />
}

export default App
