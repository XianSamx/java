import { Nav } from "react-bootstrap";
import { Navbar,Container } from "react-bootstrap";
import { Link } from "react-router-dom";
import "./Header.css"

const Header = () => {
  return (
    <>
    <Navbar bg="primary" variant="dark">
      <Container>
        <Navbar.Brand to = "/"><strong>用户登录</strong></Navbar.Brand>
        <Nav className="ml-auto"> 
            <Nav.Link as = {Link} to = "/api/users/adduser" className="nav-link">注册用户</Nav.Link>
        </Nav>
      </Container>
    </Navbar>
    </>
  )
}


export default Header;