import "./dashboard.css"
import logo from "./images/logo.svg"
import './horizontalNav.css'

import {AiFillTags} from 'react-icons/ai'
import {MdDashboard} from 'react-icons/md'
import {IoMdChatboxes} from 'react-icons/io'
import {ImHistory } from 'react-icons/im';
import {CgProfile, CgLogOut} from 'react-icons/cg';
import { Link, withRouter } from "react-router-dom"
import { React, Component } from "react"

class HorizontalNav extends Component{

    handleLogout = (event) => {
        event.preventDefault();

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/users/logout', requestOptions).then(res => res.json()).then((data) => {
            console.log(data);
            this.props.logout();
            this.props.history.push('/login');
        });

    }

    render(){
        return(
        <div>
            <ul className="nav flex-column Nav">
                        <li>
                            <img src={logo} className="img-fluid" alt="Logo" />
                        </li>
                        <li className="nav-item">
                            {(localStorage.getItem('role') === 'freelancer') ? 
                            <a className="nav-link active" href="#"><Link to="/dashboard" ><MdDashboard /> Dashboard</Link></a>
                             : <a className="nav-link active" href="#"><Link to="/customer/dashboard" ><MdDashboard /> Dashboard</Link></a>}
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="#"><Link to="/annonces" > <AiFillTags /> Annonces</Link></a>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="#"><Link to="/service-client" ><IoMdChatboxes /> Service Client</Link></a>
                        </li>
                        <li className="nav-item">
                        {(localStorage.getItem('role') === 'freelancer') ? 
                             <a className="nav-link" href="#"><Link to="/portfeuille" ><ImHistory /> Portfeuille</Link></a>
                             : <a className="nav-link active" href="#"><Link to="/customer/historique" ><ImHistory/> Historique</Link></a>}
                            
                        </li>
                        <li className="nav-item">
                        {(localStorage.getItem('role') === 'freelancer') ? 
                            <a className="nav-link" href="#"><Link to="/freelancer/profile" > <CgProfile/> Profile</Link></a>
                             : <a className="nav-link" href="#"><Link to="/customer/profile" ><CgProfile/> Profile</Link></a>}
                            
                        </li>

                        <li className="nav-item BottomNav1">
                                
                                <div class="d-inline  text-dark "> <CgProfile/> {localStorage.getItem('FirstName')} {localStorage.getItem('LastName')} </div>
                        
                        </li>
                        <li className="nav-item BottomNav">
                                <button onClick={this.handleLogout} className="btn  btn-lg  btn-block logoutButton"><CgLogOut/> Logout</button>
                        </li>
                        <div className="BottomNav">
                            
                        </div>
                    </ul>
        </div>
    )
    }
    
}

export default withRouter(HorizontalNav)