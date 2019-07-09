//
//  TidmarshDeviceViewController.swift
//  tidzam
//
//  Created by Lachihab Sabri on 01/02/2018.
//  Copyright © 2018 Lachihab Sabri. All rights reserved.
//

import UIKit
import Charts

class TidmarshDeviceViewController: UIViewController {

    var message : String = ""
    
    @IBOutlet weak var mesure: UISegmentedControl!
    @IBOutlet weak var linechart: LineChartView!
    @IBOutlet weak var period: UISegmentedControl!
    
    var month : [String] = ["January", "February", "March", "April", "May", "June", "July",
                            "August", "September", "October", "November", "December"]
    
    var months: [String] = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
    
    var daynumber : [String] = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
                                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
    
    var year : [String] = ["2017","2018"]
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        linechart.noDataText="NO DATA FOUND"
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
