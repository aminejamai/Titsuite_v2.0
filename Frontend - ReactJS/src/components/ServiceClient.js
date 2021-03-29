import HorizontalNav from "./HorizontalNav";
import {React, Component} from 'react'
import { withRouter } from "react-router-dom";
import './ServiceClient.css'
import profilePng from './images/profile.png'

class Profile extends Component{
    state = {
        title: '',
        body: ''
    }

    handleChange = (event) => {
        this.setState(
            {
                [event.target.name]: event.target.value
            }
        )
    }

    handleSubmit = (event) => {
        event.preventDefault();

        const message = {...this.state};

        console.log(message);

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(message)
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/users/sendComplaint', requestOptions)
            .then(res => res.json())
            .then((data) => {
                console.log(data);
            }
        );
    }

    render(){

        return <div className="row">
                <div className="col-2">
                    <HorizontalNav logout={this.props.rest.logout}/>
                </div>
                <div className="col Content ">
                    <div className="row ">
                        <div className="col ProfileHeader">
                            <h2 className="freelancerProfile">Customer Service</h2>
                        </div>
                    </div>
                    
                    <div className="row">
                        <div className="col-4 profileIllustration">
                            <img src={profilePng} className="img-fluid" alt="Profile" />
                        </div>
                        <div className="col-7 Content PersonalDetails">
                            
                            <form onSubmit={this.handleSubmit}>
                                <div >
                                    <div >
                                        <div className="row">
                                            <div className="col">
                                                <h3 className="freelancerProfile">Contact Us</h3>
                                            </div>
                                        </div>
                                        
                                        <div className="row">
                                            <div className="col-12">
                                                <label>Title</label>
                                                <input name="title" value={this.state.title} onChange={this.handleChange} className="form-control" required />
                                            </div>
                                            <div className="col-12">
                                                <label>Body</label>
                                                <textarea name="body" rows="6" value={this.state.body} onChange={this.handleChange} className="form-control" required />
                                            </div>
                                        </div>
                                        <hr />
                                        
                                    </div>
                                    <button type="submit" className="btn btn-primary rightFloat">Submit</button>
                                </div>
                            </form>
                        </div>
                        
                    </div>
                </div>
        </div>
    }
}

export default withRouter(Profile);