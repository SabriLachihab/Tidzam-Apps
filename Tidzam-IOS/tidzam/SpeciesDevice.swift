//
//  SpeciesDevice.swift
//  tidzam
//
//  Created by Lachihab Sabri on 25/01/2018.
//  Copyright © 2018 Lachihab Sabri. All rights reserved.
//

import UIKit
import Charts

class SpeciesDevice: UIViewController,ChartViewDelegate,UITableViewDelegate,UITableViewDataSource {
    
   
    @IBOutlet weak var periodLbl: UILabel!
    @IBOutlet weak var period: UISegmentedControl!
    @IBOutlet weak var myTableView: UITableView!
    @IBOutlet weak var barchartview: BarChartView!
    var species = [SensorCount]()
    var href = String()
    var message = String()
    var speciename = [String](["crickets", "rain", "birds","wind", "airplane", "cicadas", "frog", "mic_crackle"])
    var colors: [UIColor] = [UIColor.magenta,UIColor.cyan,UIColor.blue,UIColor.lightGray,UIColor.darkGray,UIColor.orange,UIColor.green,UIColor.red]
    
    var month : [String] = ["January", "February", "March", "April", "May", "June", "July",
                            "August", "September", "October", "November", "December"]
    
    var months: [String] = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
    
    var daynumber : [String] = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
                                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
    
    var year : [String] = ["2017","2018"]
    
    @IBAction func refresh_the_graph(_ sender: UIBarButtonItem) {
        printbarchart()
    }
    @IBOutlet weak var navigationbarcustom: UINavigationItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.myTableView.delegate = self
        self.myTableView.dataSource = self
        self.periodLbl.text = "All of time"
        listspecies()
    }
    
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
            let cell = myTableView.dequeueReusableCell(withIdentifier: "legendcase") as! Legend
            cell.specielabel.text = speciename[indexPath.row]
            cell.imagecolor.backgroundColor = colors[indexPath.row]
            cell.specieimage.image = UIImage(named: speciename[indexPath.row])
            return cell
        }
        
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
            return speciename.count
        }
        
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
            _ = indexPath.row
            if(speciename[(myTableView.indexPathForSelectedRow?.row)!] != "birds")
            {
                performSegue(withIdentifier: "wikishow", sender: self)
            }
            else
            {
                performSegue(withIdentifier: "birdshow", sender: self)
            }
        }
        
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
            if let destination = segue.destination  as? WikiViewController
            {
                destination.specie=speciename[(myTableView.indexPathForSelectedRow?.row)!]
            } 
            if let destination = segue.destination as? BirdsViewController
            {
                destination.speciecount = species
            }
        }
        
    override func didReceiveMemoryWarning() {
            super.didReceiveMemoryWarning()
        }
        
    func listspecies()
        {
            let idsensors = href.split(separator: "/")
            print(idsensors)
            let url = "http://chain-api.media.mit.edu/sensors/?offset=3000&limit=0&device_id="+idsensors[idsensors.count-1]
            let request = NSMutableURLRequest(url: URL(string: url)!)
            request.httpMethod = "GET"
            
            let requestAPI = URLSession.shared.dataTask(with: request as URLRequest)
            {
                data, response, error in
                if (error != nil) {
                    print(error!.localizedDescription) // On indique dans la console ou est le problème dans la requête
                }
                let responseAPI = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
                //print("responseString = \(responseAPI)") // Affiche dans la console la réponse de l'API
                if let jsonObj = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                {
                    //printing the json in console
                    //print(jsonObj!.value(forKey: "_links")!)
                    if let items = jsonObj!.value(forKey: "_links") as? NSDictionary
                    {
                        if let items_ = items.value(forKey: "items") as? NSArray
                        {
                            for item in items_
                            {
                                let specie = SensorCount()
                                if let name = item as? NSDictionary
                                {
                                    if var name_ = name.value(forKey: "title")
                                    {
                                        specie.specie=name_ as! String
                                        if let href = name.value(forKey: "href")
                                        {
                                            specie.href=href as! String
                                        }
                                        self.species.append(specie)
                                    }
                                }
                            }
                            
                        }
                    }
                }
                if error == nil {
                    // Ce que vous voulez faire.
                }
                print(self.species.count)
                self.Countbysensors()
            }
            requestAPI.resume()
        }
        
    func Countbysensors() -> Void {
        DispatchQueue.main.async(execute: {
            for i in 0..<self.species.count
            {
                let idsensors = self.species[i].href.split(separator: "/")
                print(idsensors)
                let url = "http://chain-api.media.mit.edu/aggregate_data/?sensor_id="+idsensors[idsensors.count-1]+"&aggtime=1d"
                let request = NSMutableURLRequest(url: URL(string: url)!)
                request.httpMethod = "GET"
                let requestAPI = URLSession.shared.dataTask(with: request as URLRequest)
                {
                    data, response, error in
                    if (error != nil) {
                        print(error!.localizedDescription) // On indique dans la console ou est le problème dans la requête
                    }
                    let responseAPI = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
                    //print("responseString = \(responseAPI)") // Affiche dans la console la réponse de l'API
                    if let jsonObj = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                    {
                        //printing the json in console
                        //print(jsonObj!.value(forKey: "_links")!)
                        if let items = jsonObj!.value(forKey: "data") as? NSArray
                        {
                            for item in items
                            {
                                let specie = SensorCount()
                                if let name = item as? NSDictionary
                                {
                                    if let countday = name.value(forKey: "count")
                                    {
                                        let cc = countday as! Int
                                        if let timestap = name.value(forKey: "timestamp")
                                        {
                                            let datet = timestap as! String
                                            self.species[i].date.append(datet)
                                            self.species[i].count.append(cc)
                                        }
                                        self.species[i].verify=true
                                    }
                                }
                            }
                            print(self.species[i].specie)
                        }
                        if(i==self.species.count-1 && self.species[self.species.count-1].verify == true)
                        {
                            self.printbarchart()
                        }
                    }
                    if error == nil {
                        // Ce que vous voulez faire.
                    }
                }
                requestAPI.resume()
            }
        })
    }
        
    @objc func printbarchart() -> Void {
            DispatchQueue.main.async(execute: {
                var dataEntries: [BarChartDataEntry] = []
                if(self.periodLbl.text=="All of time")
                {
                    dataEntries.append(BarChartDataEntry(x: 0, yValues: self.barbycat(date: "2017")))
                    dataEntries.append(BarChartDataEntry(x: 1, yValues: self.barbycat(date: "2018")))
                    self.barchartview.xAxis.valueFormatter = IndexAxisValueFormatter(values: self.year)
                }
                else if(self.period.selectedSegmentIndex==1)
                {
                    for i in 0..<12
                    {
                        dataEntries.append(BarChartDataEntry(x: Double(i), yValues: self.barbycat(date: self.periodLbl.text!+"-"+self.daynumber[i])))
                        self.barchartview.xAxis.valueFormatter = IndexAxisValueFormatter(values: self.months)

                    }
                }
                else if(self.period.selectedSegmentIndex==2)
                {
                    var separator : [String] = ((self.periodLbl.text)?.components(separatedBy: " "))!
                    print(separator)
                    var index_month : Int = 0
                    for i in 0 ..< self.month.count
                    {
                        if(separator[0].elementsEqual(self.month[i]))
                        {
                            index_month = i
                        }
                    }
                    for i in 0..<self.daynumber.count
                    {
                        dataEntries.append(BarChartDataEntry(x: Double(i), yValues: self.barbycat(date: separator[1]+"-"+self.daynumber[index_month]+"-"+self.daynumber[i])))
                        self.barchartview.xAxis.valueFormatter = IndexAxisValueFormatter(values: self.months)
                    }
                    
                }
                self.barchartview.delegate=self
                let chartDataSet = BarChartDataSet(values: dataEntries, label: "")
                chartDataSet.colors=self.colors
                let dataset = BarChartData(dataSet: chartDataSet)
                dataset.setDrawValues(false)
                self.barchartview.legend.enabled=false
                self.barchartview.xAxis.granularityEnabled = true
                self.barchartview.xAxis.drawGridLinesEnabled = false
                self.barchartview.drawValueAboveBarEnabled=true
                self.barchartview.xAxis.labelPosition = .bottom
                self.barchartview.leftYAxisRenderer.accessibilityElementsHidden=true
                self.barchartview.data=dataset
                self.barchartview.animate(yAxisDuration: 3.0)
            })
        }
    
    @IBAction func indexChanged(sender: UISegmentedControl) {
        switch period.selectedSegmentIndex
        {
        case 0 :
            self.periodLbl.text="All of time"
            self.printbarchart()
            break
        //show popular view
        case 1 :
            let alert = UIAlertController(title: "YEAR",
                                          message: "choose a year",
                                          preferredStyle: .actionSheet)
            let action1 = UIAlertAction(title: "2017", style: .default, handler: { (action) -> Void in
                self.periodLbl.text="2017"
                self.printbarchart()
            })
            
            let action2 = UIAlertAction(title: "2018", style: .default, handler: { (action) -> Void in
                self.periodLbl.text="2018"
                self.printbarchart()
            })
            
            // Cancel button
            let cancel = UIAlertAction(title: "Cancel", style: .destructive, handler: { (action) -> Void in })
            alert.addAction(action1)
            alert.addAction(action2)
            alert.addAction(cancel)
            present(alert, animated: true, completion: nil)
            break
        case 2 :
            let alert = UIAlertController(title: "Month",
                                          message: "choose a month",
                                          preferredStyle: .actionSheet)
            let cancel = UIAlertAction(title: "Cancel", style: .destructive, handler: { (action) -> Void in })
            for i in 0..<12
            {
                alert.addAction(UIAlertAction(title: month[i], style: UIAlertActionStyle.default, handler: { (action) ->Void in
                    let alert1 = UIAlertController(title: action.title,
                                                  message: "choose a year",
                                                  preferredStyle: .actionSheet)
                    
                    
                    for j in 0..<self.year.count
                    {
                        alert1.addAction(UIAlertAction(title: self.year[j], style: UIAlertActionStyle.default, handler: { (action) -> Void in
                            self.periodLbl.text = self.month[i]+" "+self.year[j]
                            self.printbarchart()
                        }))
                    }
                    alert1.addAction(cancel)
                    self.present(alert1, animated: true, completion: nil)
                })
            )}
            alert.addAction(cancel)
            present(alert, animated: true, completion: nil)
            break
        case 4:
            break
        default:
            break;
        }
    }
    
    func barbycat(date : String) -> [Double] {
        var yvalues : [Double] = []
        for i in 0..<self.speciename.count
        {
            for j in 0..<self.species.count
            {
                if(self.species[j].specie==self.speciename[i])
                {
                    var speciecount : Double = 0
                    for l in 0..<self.species[j].date.count
                    {
                        if self.species[j].date[l].contains(date)
                        {
                            speciecount = speciecount + Double(self.species[j].count[l])
                        }
                    }
                    yvalues.append(Double(speciecount))
                }
            }
        }
        print(yvalues)
        return yvalues
    }
    
    public func stringForValue(value: Double,tab : [String] ,axis: AxisBase?) -> String {
        return tab[Int(value)]
    }
}

