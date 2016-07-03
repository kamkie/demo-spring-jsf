import ReactDOM from "react-dom";
import React from "react";
import Table from "./table";
import "../css/general.css";

ReactDOM.render(
    <div>
        <Table number={1} openSeats={[1, 2]}/>
        <Table number={2} openSeats={[1, 2, 3]}/>
        <Table number={3} openSeats={[1]}/>
        <Table number={4} openSeats={[1, 2, 3, 4]}/>
        <Table number={5} openSeats={[1, 2, 3, 4]}/>
        <Table number={6} openSeats={[1, 2, 3]}/>
    </div>,
    document.getElementById('content')
);
