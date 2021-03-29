import './Login.css'
import logo from './images/logo.svg'
import pict from './images/login_pict.svg'
import {React, Component} from 'react'
import { Link, Redirect, withRouter} from 'react-router-dom';
import LandingPage from './LandingPage';
class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {
            email: '',
            password: '',
            role: ''
        };
    }

    handleChange = (event) => {
        this.setState(
            {
                [event.target.name]: event.target.value
            }
        );
    }

    handleSubmit = event => {
        event.preventDefault();
    
        const user = {
            email: this.state.email,
            password: this.state.password,
            role: this.state.role
        };

        
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/users/login', requestOptions)
            .then(res => res.json())
            .then((data) => {
                this.props.rest.authenticate();
                let value = this.state.role;
                localStorage.setItem('role', value);
                this.props.history.push('/' + this.state.role + '/profile');
            });
        
        
    }

    render(){
        return (
        <div className="Login">
           <div className="container">
                <div className="row">
                    <div className="col FormDiv">
                        <img src={logo} className="img-fluid" alt="Logo" />

                        <div className="LoginContent">
                            <p > <span><b>Login </b>
                                <br/>Access your Account</span></p>
                        </div>
                        <div className="LoginForm">
                            <form onSubmit={this.handleSubmit}>
                                <div className="form-group">
                                    <label htmlFor="exampleInputEmail1">Email address</label>
                                    <input type="email" className="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" placeholder="Enter email" name="email" onChange={this.handleChange} required/>
                                    <small id="emailHelp" className="form-text text-muted">We'll never share your email with anyone else.</small>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="exampleInputPassword1">Password</label>
                                    <input type="password" className="form-control" id="exampleInputPassword1" placeholder="Password" name="password" onChange={this.handleChange} required autoComplete="true" />
                                </div>

                                <div className="form-check">
                                    <input className="form-check-input" type="radio" name="role" id="exampleRadios1" value="customer" onChange={this.handleChange} checked={this.state.role === "customer"} />
                                    <label className="form-check-label" htmlFor="exampleRadios1">
                                        I'm a Client
                                    </label>
                                </div>
                                <div className="form-check">
                                    <input className="form-check-input" type="radio" name="role" id="exampleRadios2" value="freelancer" onChange={this.handleChange} checked={this.state.role === "freelancer"}/>
                                    <label className="form-check-label" htmlFor="exampleRadios2">
                                        I'm a FreeLancer
                                    </label>
                                    <br/>
                                </div>
                                    
                                
                                <div className="row">
                                    <div className="col"><button type="submit" className="btn btn-primary" id="submitButton">Submit</button></div>
                                    <div className="col">
                                        <div><span><a href="" className="stretched-link">Forget your Password?</a></span></div>
                                        <div><span><a href="" className="stretched-link">Don’t have an Account?</a></span></div>
                                    </div>
                                </div>
                            </form>
                        </div>
                        
                    </div>
                    <div className="col illustration">
                        <center>
                            <h3>Tous nos services au bout du doigt</h3>
                            <p>Laissez-nous gérer ce qui vous prend du temps et concentrez-vous sur l’essentiel !</p>
                        </center>
                        

                        <img src={pict} className="img-fluid" alt="Logo" />
                    </div>
                </div>
            </div>
        </div>
        
    )
    }
;}

export default  withRouter(Login);