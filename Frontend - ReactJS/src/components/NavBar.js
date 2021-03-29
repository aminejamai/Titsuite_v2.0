import './navBar.css'
import logo from './images/logo.svg'
import { Link } from 'react-router-dom';

function NavBar() {
    return (
      <div>
        <nav className="navbar navbar-expand-sm navbar-light navbar-css">
            <a className="navbar-brand" href="#"><img src={logo} className="img-fluid" alt="TistSuite" /></a>
            <button className="navbar-toggler d-lg-none" type="button" data-toggle="collapse" data-target="#collapsibleNavId" aria-controls="collapsibleNavId"
                aria-expanded="false" aria-label="Toggle navigation"></button>
              <div className="collapse navbar-collapse" id="collapsibleNavId">
                  <ul className="navbar-nav  ml-auto mt-2 mt-lg-0">  
                    <li className="nav-item ">
                        <a className="nav-link" href="#"> Careers <span className="sr-only">(current)</span></a>
                    </li>
                    <li className="nav-item">
                        <a className="nav-link" href="#"> Services </a>
                    </li>
                    <li className="nav-item ">
                        <a className="nav-link" href="#">About us <span className="sr-only">(current)</span></a>
                    </li>
                    <li className="nav-item ">
                        <a className="nav-link" href="#">Team <span className="sr-only">(current)</span></a>
                    </li>
                    <li className="nav-item ">
                        <a className="nav-link" href="#"> <Link to="/login">Login</Link> <span className="sr-only">(current)</span></a>
                    </li>
                    <li className="nav-item active">
                        <a className="nav-link" href="#"><Link to="/signup">Sign up </Link><span className="sr-only">(current)</span></a>
                    </li>
                  </ul>
            </div>
        </nav>
      </div>
    );
  }
  
export default NavBar;