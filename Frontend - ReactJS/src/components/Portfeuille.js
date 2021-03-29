import { render } from "@testing-library/react";
import { React, Component } from "react";
import { withRouter } from "react-router-dom";
import HorizontalNav from "./HorizontalNav";
import './portfeuille.css'
import {BsCalendar, BsSearch} from 'react-icons/bs'
import {IoMdLocate, IoIosNotifications} from 'react-icons/io'
import {FaMoneyBillWave, FaRegComments} from 'react-icons/fa'
import {RiFolderUserLine} from 'react-icons/ri'
import {MdDescription} from 'react-icons/md'
import portfeuille from './images/portefeuille.svg'

export default class Portfeuille extends Component {
    state ={
        items : []
    }

    componentDidMount() {
        const requestOptions = {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/myjobs/all', requestOptions).then(res => res.json()).then((data) => {
            console.log(data);
            this.setState(
                {items : data}
            )
        });
    }
    
    render(){

        const offers = ()=>{
            return this.state.items.map((item) => {
                return (<div className="serviceItem">
                    <h6><MdDescription/> {item.description} </h6> 
                    <hr/>
                    <RiFolderUserLine /> Mr. {item.last_name} {item.first_name} <br/>
                    <IoMdLocate /> {item.city} <br/>
                    <FaMoneyBillWave /> {item.minimumWage} <br/>
                    <BsCalendar /> {item.period} <br/>
                    <FaRegComments /> {item.review}
                </div>
                )
            })
        }
        return (
        <div>
            <div className="row">
                <div className="col-2">
                    <HorizontalNav logout={this.props.rest.logout}/>
                </div>
                <div className="col Content">
                    <div className="row">
                        <div className="col-6">
                            <div className="Header"><b>My Services</b></div>
                            <hr />
                            <div >
                                    {offers()}
                            </div>
                        </div>
                        <div className="col">
                            <div className="Header">
                                <b>Saturday, February 27th</b>
                                <hr />
                            </div>

                            <div className="row">
                                <div className="col img_port">
                                 <img src={portfeuille} className="img-fluid" alt="Logo" />
                                </div>
                                
                            </div>
                        </div>
                    </div>

                    
                </div>
            </div>
            
        </div>
        
    )
    }
    ;}

// export default  Portfeuille ;