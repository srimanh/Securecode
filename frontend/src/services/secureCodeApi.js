const API_BASE_URL = 'http://localhost:8080/api';

/**
 * Sends code to the backend for security analysis.
 * @param {string} code - The code snippet to analyze.
 * @returns {Promise<Object>} - The structured analysis response.
 */
export const analyzeCode = async (code) => {
    try {
        const response = await fetch(`${API_BASE_URL}/analyze`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ input: code }),
        });

        if (!response.ok) {
            throw new Error(`Server error: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Analysis API Error:', error);
        throw error;
    }
};
