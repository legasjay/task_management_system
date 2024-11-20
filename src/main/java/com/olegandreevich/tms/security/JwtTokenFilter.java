package com.olegandreevich.tms.security;

//@Component
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    private final Algorithm algorithm;
//
//    public JwtTokenFilter(Algorithm algorithm) {
//        this.algorithm = algorithm;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//        String token = getJwtFromRequest(request);
//
//        if (token != null && JWT.require(algorithm).build().verify(token)) {
//            String username = JWT.decode(token).getSubject();
//            List<String> authorities = JWT.decode(token).getClaim("roles").asList(String.class);
//
//            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                    username,
//                    null,
//                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }
//
//        chain.doFilter(request, response);
//    }
//
//    // Метод для извлечения JWT-токена из запроса
//    private String getJwtFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        } else {
//            throw new RuntimeException("JWT Token not found in Authorization header");
//        }
//    }
//}
