Bad example:

public void getUserInfo(HttpServletRequest request) {   
    String userId = request.getParameter("userid"); 
    String userRole = request.getParameter("role");
    
    // CRITICAL VULNERABILITY: 
    // Direct string concatenation allows an attacker to bypass authentication
    // or leak the entire database by passing "' OR '1'='1" as input.
    String sql = "SELECT * FROM accounts WHERE id = " + userId + " AND role = '" + userRole + "'";
    
    try (Connection conn = db.getConnection();
         Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println("Result: " + rs.getString("email"));
        }
    } catch (Exception e) {
        e.printStackTrace(); // Information Leakage
    }
}

Good example:

public void getUserInfoSecurely(HttpServletRequest request) {
    String userId = request.getParameter("userid");
    
    // SECURE: Using a Parameterized Query (PreparedStatement)
    // The database driver handles the input as data, not executable code.
    String sql = "SELECT email FROM accounts WHERE id = ?";
    
    try (Connection conn = db.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, userId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                logger.info("Found user record");
            }
        }
    } catch (SQLException e) {
        logger.error("Database error occurred"); // Safe error handling
    }
}