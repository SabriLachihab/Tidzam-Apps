//
//  BirdsViewController.swift
//  tidzam
//
//  Created by Lachihab Sabri on 28/01/2018.
//  Copyright © 2018 Lachihab Sabri. All rights reserved.
//

import UIKit
import Charts


class BirdsViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    

    

    @IBOutlet weak var piechartvview: PieChartView!
    @IBOutlet weak var legendbirds: UITableView!
    
    var birdscount : [SensorCount] = []
    
    var birdsname : [String] = ["mallard", "northern_cardinal", "red_winged_blackbird", "mourning_dove",
                                "downy_woodpecker", "herring_gull", "american_crow", "song_sparrow", "american_robin",
                                "black_capped_chickadee", "tufted_titmouse", "american_goldfinch", "canada_goose",
                                "barn_swallow", "blue_jay"]
    
    var speciecount : [SensorCount] = []
    
    var colors : [UIColor] = [UIColor.magenta,UIColor.cyan,UIColor.blue,UIColor.black,UIColor.darkGray,UIColor.lightGray,UIColor.gray,UIColor.orange,UIColor.green,UIColor.red,UIColor.yellow,UIColor.purple,UIColor.brown,UIColor.init(red: 51.0, green: 153.0, blue: 250.0, alpha: 1.0),UIColor.init(red: 153.0, green: 250.0, blue: 153.0, alpha: 1.0),UIColor.init(red: 0.0, green: 102.0, blue: 51.0, alpha: 1.0)]
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        legendbirds.delegate=self
        legendbirds.dataSource=self
        printpiechart()
        
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "legendcase") as! Legend
        cell.specielabel.text = birdsname[indexPath.row]
        cell.imagecolor.backgroundColor = colors[indexPath.row]
        cell.specieimage.image = UIImage(named: birdsname[indexPath.row])
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
            performSegue(withIdentifier: "birdwiki", sender: self)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination  as? WikiViewController
        {
            destination.specie=birdsname[(legendbirds.indexPathForSelectedRow?.row)!]
        }
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return birdsname.count
    }
    
    func printpiechart()
    {
        for i in 0..<birdsname.count
        {
            let bird = SensorCount()
            bird.specie=birdsname[i]
            bird.count = []
            birdscount.append(bird)
        }
        for i in 0..<birdscount.count
        {
            for j in 0..<speciecount.count
            {
                if(speciecount[j].specie.contains(birdscount[i].specie))
                {
                    var countbird : Int = 0
                    for l in 0..<speciecount[j].date.count
                    {
                        countbird += speciecount[j].count[l]
                    }
                    birdscount[i].count.append(countbird)
                }
            }
        }
        var dataentries : [ChartDataEntry] = []
        for i in 0..<birdscount.count
        {
            let birdentry = ChartDataEntry(x: Double(i), y: Double(birdscount[i].count[0]))
            dataentries.append(birdentry)
        }
        let piedataset =  PieChartDataSet(values: dataentries, label: "birds")
        let formatter = NumberFormatter()
        formatter.numberStyle = .percent
        formatter.maximumFractionDigits = 1
        formatter.multiplier = 1.0
        formatter.percentSymbol = "%"
        formatter.zeroSymbol = ""
        piedataset.colors = colors
        let piechartdata = PieChartData(dataSet: piedataset)
        piechartdata.setValueFormatter(DefaultValueFormatter(formatter: formatter))
        self.piechartvview.usePercentValuesEnabled=true
        self.piechartvview.data=piechartdata
        self.piechartvview.delegate = self as? ChartViewDelegate
        self.piechartvview.animate(yAxisDuration: 3)
    }
    
    func chartValueSelected(chartView: ChartViewBase, entry: ChartDataEntry, dataSetIndex: Int, highlight: Highlight) {
        self.piechartvview.centerText=birdsname[Int(entry.x)]
    }
}
