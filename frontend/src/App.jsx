import { useState } from 'react';
import './App.css';
import { analyzeCode } from './services/secureCodeApi';

function App() {
  const [code, setCode] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const EXAMPLES = [
    {
      id: 'sql',
      label: 'SQL Injection',
      code: 'String query = "SELECT * FROM users WHERE id = " + userId;'
    },
    {
      id: 'secret',
      label: 'Hardcoded Secret',
      code: 'String apiKey = "sk_test_123456";'
    },
    {
      id: 'validation',
      label: 'Missing Validation',
      code: 'int age = Integer.parseInt(request.getParameter("age"));'
    }
  ];

  const handleAnalyze = async (inputCode = code) => {
    if (!inputCode.trim()) return;

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const data = await analyzeCode(inputCode);
      setResult(data);
    } catch (err) {
      setError('Unable to analyze at this time. Please ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  const loadExample = (exampleCode) => {
    setCode(exampleCode);
    handleAnalyze(exampleCode);
  };

  const getStatusClass = () => {
    if (!result) return '';
    if (result.message) return 'status-refusal';
    return result.isSecure ? 'status-secure' : 'status-insecure';
  };

  const getStatusText = () => {
    if (!result) return '';
    if (result.message) return 'Not Covered';
    return result.isSecure ? 'Secure' : 'Insecure';
  };

  return (
    <div className="container">
      <header>
        <h1>SecureCode Analysis</h1>
        <p className="description">AI-powered security policy enforcement for your code</p>
      </header>

      <main className="analysis-container">
        <section className="editor-area">
          <textarea
            placeholder="Paste your code snippet here..."
            value={code}
            onChange={(e) => setCode(e.target.value)}
            disabled={loading}
          />
          <div className="examples-section">
            <span className="example-label">Try an Example:</span>
            <div className="example-buttons">
              {EXAMPLES.map((ex) => (
                <button
                  key={ex.id}
                  className="btn-example"
                  onClick={() => loadExample(ex.code)}
                  disabled={loading}
                >
                  {ex.label}
                </button>
              ))}
            </div>
          </div>
          <div className="actions">
            <button onClick={() => handleAnalyze()} disabled={loading || !code.trim()}>
              {loading ? (
                <>
                  <span className="loading"></span>
                  Analyzing...
                </>
              ) : 'Analyze Code'}
            </button>
          </div>
        </section>

        <section className="result-panel">
          {!result && !error && !loading && (
            <div className="empty-state">
              Results will appear here after analysis
            </div>
          )}

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          {result && (
            <>
              <div className="result-header">
                <span className={`status-badge ${getStatusClass()}`}>
                  {getStatusText()}
                </span>
                <span className="policy-category">
                  {result.policyCategory || (result.message ? 'Out of Scope' : 'N/A')}
                </span>
              </div>

              {result.message ? (
                <div className="result-section">
                  <div className="section-title">Backend Message</div>
                  <div className="section-content">
                    {result.message}
                  </div>
                </div>
              ) : (
                <>
                  {!result.isSecure && (
                    <div className="result-section">
                      <div className="section-title">Issue Isolated</div>
                      <div className="section-content" style={{ color: 'var(--accent-red)', fontWeight: 600 }}>
                        {result.issue}
                      </div>
                    </div>
                  )}

                  <div className="result-section">
                    <div className="section-title">Explanation</div>
                    <div className="section-content">
                      {result.explanation}
                    </div>
                  </div>

                  {result.safeAlternative && (
                    <div className="result-section">
                      <div className="section-title">Safer Alternative</div>
                      <div className="code-block">
                        <pre>{result.safeAlternative}</pre>
                      </div>
                    </div>
                  )}
                </>
              )}
            </>
          )}
        </section>
      </main>
    </div>
  );
}

export default App;
