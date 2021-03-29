import { React, Component } from "react";
import { withRouter } from "react-router-dom";
import HorizontalNav from "./HorizontalNav";
import cardTest from './images/cardTest.svg'
import {BsCalendar, BsSearch} from 'react-icons/bs'
import {IoMdLocate, IoIosNotifications} from 'react-icons/io'
import {FaMoneyBillWave} from 'react-icons/fa'
import {GrUserWorker} from 'react-icons/gr'

import "./mesAnnonces.css"

class MesAnnonces extends Component {

    state = {
        items : [],
        size : 0
    }
    
    componentDidMount() {
        const requestOptions = {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/offers', requestOptions)
        .then(res => res.json())
        .then((data) => {
            console.log(data);
            this.setState(
                {
                    items : data,
                    size : data.length
                }
            )
        });
    }

    render(){
        const offers = ()=>{
            return this.state.items.map((item) => {
                return (
                    <div class="card text-dark bg-light mb-3" >
                        <div class="card-header"><BsCalendar /> {new Date(item.startDay).toLocaleString()}</div>
                        <div class="card-body">
                            <h6 class="card-title">{item.description}</h6>
                            <p class="card-text">
                                <IoMdLocate /> {item.city}. <br /> 
                                <GrUserWorker /> {item.activity}. <br />
                                <FaMoneyBillWave /> {item.minimumWage} Dh. 
                            </p>
                        </div>
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
                            <div className="col filterRow">
                                <span class="overview"> <BsSearch/> | {this.state.size} services available</span>
                                <span id="recent"> Recent | <IoIosNotifications/> </span>
                            </div>
                        </div>

                        <div className="row toBeCentered">
                            {offers()}   
                        </div>
                    </div>
                </div>
                
            </div>
            )
    };
    
}

export default  withRouter(MesAnnonces) ;